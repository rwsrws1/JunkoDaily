package com.example.learncompose.core.designsystem.components.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import com.example.learncompose.core.designsystem.components.FloatAnimatableSaver
import kotlinx.coroutines.launch

@Composable
fun ScribbleStrokeCard(
    aspectRatio: Float = 2f / 3f,
    frontFaceColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    backFaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    scribbleColor: Color = MaterialTheme.colorScheme.primary,
    frontFaceContent: @Composable ColumnScope.() -> Unit = {},
    backFaceContent: @Composable ColumnScope.() -> Unit = {}
) {
    var isShowingBack by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 绘制进度 (0f -> 1f) 与 整体缩放回弹
    val scribbleProgress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }
    val scribbleAlpha = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(1f) }
    val cardScale = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(1f) }

    val handleScribble = {
        scope.launch {
            scribbleAlpha.snapTo(1f)
            scribbleProgress.snapTo(0f)

            // 1. 轻微按压抖动
            launch {
                cardScale.animateTo(0.96f, tween(150, easing = FastOutSlowInEasing))
                cardScale.animateTo(1f, tween(200, easing = LinearOutSlowInEasing))
            }

            // 2. 疯狂涂抹动画，笔划铺满
            scribbleProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )

            // 3. 在完全涂满的掩护下翻转内部状态
            isShowingBack = !isShowingBack

            // 4. 涂鸦笔触淡化散去，呈现新卡面
            scribbleAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 260, easing = LinearOutSlowInEasing)
            )
            scribbleProgress.snapTo(0f)
        }
    }

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                scaleX = cardScale.value
                scaleY = cardScale.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!scribbleProgress.isRunning) handleScribble()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isShowingBack) backFaceColor else frontFaceColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 底层内容
            Column(modifier = Modifier.fillMaxSize()) {
                if (isShowingBack) backFaceContent() else frontFaceContent()
            }

            // 顶层涂抹 Canvas
            if (scribbleProgress.value > 0f) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = scribbleAlpha.value }
                ) {
                    val w = size.width
                    val h = size.height
                    val linesCount = 14
                    val stepY = h / linesCount

                    // 构造连续的 Z 字形密集折线
                    val fullPath = Path().apply {
                        moveTo(0f, 0f)
                        for (i in 0..linesCount) {
                            val y = i * stepY
                            val x = if (i % 2 == 0) w * 1.05f else -w * 0.05f
                            lineTo(x, y)
                        }
                    }

                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(fullPath, false)
                    val totalLength = pathMeasure.length

                    val extractPath = Path()
                    pathMeasure.getSegment(0f, totalLength * scribbleProgress.value, extractPath, true)

                    drawPath(
                        path = extractPath,
                        color = scribbleColor,
                        style = Stroke(
                            width = stepY * 1.55f, // 保证上下折线完全重叠覆盖
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}