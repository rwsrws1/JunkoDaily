package com.junko.junkodaily.core.designsystem.components.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import com.junko.junkodaily.core.designsystem.components.FloatAnimatableSaver
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpiralScribbleCard(
    aspectRatio: Float = 2f / 3f,
    frontFaceColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    backFaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    scribbleColor: Color = MaterialTheme.colorScheme.tertiary,
    frontFaceContent: @Composable ColumnScope.() -> Unit = {},
    backFaceContent: @Composable ColumnScope.() -> Unit = {}
) {
    var isShowingBack by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val expandProgress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }
    val scribbleAlpha = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(1f) }
    val shakeOffset = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }

    val handleSpiral = {
        scope.launch {
            scribbleAlpha.snapTo(1f)
            expandProgress.snapTo(0f)

            // 笔触振动效果
            launch {
                repeat(4) {
                    shakeOffset.animateTo(4f, tween(50))
                    shakeOffset.animateTo(-4f, tween(50))
                }
                shakeOffset.animateTo(0f, tween(50))
            }

            // 旋涡展开
            expandProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing)
            )

            isShowingBack = !isShowingBack

            // 涂鸦消散
            scribbleAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 240)
            )
            expandProgress.snapTo(0f)
        }
    }

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                translationX = shakeOffset.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!expandProgress.isRunning) handleSpiral()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isShowingBack) backFaceColor else frontFaceColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isShowingBack) backFaceContent() else frontFaceContent()
            }

            if (expandProgress.value > 0f) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = scribbleAlpha.value }
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = kotlin.math.hypot(size.width, size.height) / 1.7f
                    val totalLoops = 14
                    val path = Path()

                    var isFirst = true
                    val steps = 360
                    for (i in 0..steps) {
                        val angle = i * (totalLoops * 2f * Math.PI.toFloat()) / steps
                        val r = (i.toFloat() / steps) * maxRadius
                        // 添加微小摆动使其呈现手绘涂鸦质感
                        val jitter = sin(i * 1.5f) * 6f
                        val x = center.x + (r + jitter) * cos(angle)
                        val y = center.y + ((r + jitter) * 1.4f) * sin(angle)

                        if (isFirst) {
                            path.moveTo(x, y)
                            isFirst = false
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    val measure = PathMeasure()
                    measure.setPath(path, false)
                    val drawSegment = Path()
                    measure.getSegment(0f, measure.length * expandProgress.value, drawSegment, true)

                    drawPath(
                        path = drawSegment,
                        color = scribbleColor,
                        style = Stroke(
                            width = 46f,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }
    }
}