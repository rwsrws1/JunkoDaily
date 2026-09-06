package com.example.learncompose.core.designsystem.components.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.learncompose.core.designsystem.components.FloatAnimatableSaver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalCardScopeProvider = compositionLocalOf<CoroutineScope?> {
    null
}

@Composable
fun ScratchMaskCard(
    aspectRatio: Float = 2f / 3f,
    frontFaceColor: Color = MaterialTheme.colorScheme.primaryContainer,
    backFaceColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    frontFaceContent: @Composable ColumnScope.() -> Unit = {},
    backFaceContent: @Composable ColumnScope.() -> Unit = {}
) {
    var isShowingBack by rememberSaveable { mutableStateOf(false) }
    // 标记擦除过渡中的目标状态
    var isErasing by rememberSaveable { mutableStateOf(false) }
    val scope = LocalCardScopeProvider.current

    val scratchProgress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }

    val handleScratch = {
        scope?.launch {
            isErasing = true
            scratchProgress.snapTo(0f)
            // 1. 执行刮除动画
            scratchProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            )
            // 2. 状态切换完成
            isShowingBack = !isShowingBack
            scratchProgress.snapTo(0f)
            isErasing = false
        }
    }

    // 确定当前底层（新）和顶层（旧/正在被刮掉的）各是什么
    val topShowingBack = if (isErasing) isShowingBack else isShowingBack
    val bottomShowingBack = if (isErasing) !isShowingBack else isShowingBack

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!scratchProgress.isRunning) handleScratch()
            }.border(2.dp, color = MaterialTheme.colorScheme.onSurface, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = if (bottomShowingBack) backFaceColor else frontFaceColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 底层：露出显示的内容
            Column(modifier = Modifier.fillMaxSize()) {
                if (bottomShowingBack) backFaceContent() else frontFaceContent()
            }

            // 顶层：被刮除的图层（仅在擦除进行中叠加渲染与裁剪）
            if (isErasing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // 开启 Offscreen 合成图层，使内部的 DstOut 仅扣除本图层内容
                            compositingStrategy = CompositingStrategy.Offscreen
                        }
                ) {
                    // 顶层原本的内容与底色
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (topShowingBack) backFaceColor else frontFaceColor
                        )
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            if (topShowingBack) backFaceContent() else frontFaceContent()
                        }
                    }

                    // 擦除笔刷：必须使用不透明色彩（Alpha > 0），通过 DstOut 挖空当前图层
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val rows = 10
                        val gap = h / rows

                        val scratchPath = Path().apply {
                            moveTo(-w * 0.1f, 0f)
                            for (i in 0..rows) {
                                val y = i * gap
                                val x = if (i % 2 == 0) w * 1.15f else -w * 0.15f
                                lineTo(x, y)
                            }
                        }

                        val pm = PathMeasure()
                        pm.setPath(scratchPath, false)
                        val eraseSegment = Path()
                        pm.getSegment(0f, pm.length * scratchProgress.value, eraseSegment, true)

                        drawPath(
                            path = eraseSegment,
                            color = Color.Black, // 必须是不透明颜色，才能起到擦除 Alpha 的效果
                            style = Stroke(
                                width = gap * 2.0f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            ),
                            blendMode = BlendMode.DstOut
                        )
                    }
                }
            }
        }
    }
}