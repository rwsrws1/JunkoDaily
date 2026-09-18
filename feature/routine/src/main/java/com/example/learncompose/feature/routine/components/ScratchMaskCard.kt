package com.example.learncompose.feature.routine.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ScratchMaskCard(
    aspectRatio: Float = 2f / 3f,
    frontFaceColor: Color = Color.Unspecified,
    backFaceColor: Color = Color.Unspecified,
    onFrontFaceClick: () -> Unit = {},
    onBackFaceClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    isFrontFace: Boolean = true,
    content: @Composable () -> Unit = {}
) {

    val scope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false)  }
    val scratchProgress = remember { Animatable(0f) }
    val layerPaint = remember { Paint() }
    val staticColor = if (isFrontFace) frontFaceColor else backFaceColor

    var freezeFlag by remember { mutableStateOf(true)  }
    // 动画运行期间冻结“起点颜色(currentColor)”和“终点颜色(nextColor)”
    val currentColor = remember(freezeFlag) {
        if (isFrontFace) frontFaceColor else backFaceColor
    }
    val nextColor = remember(freezeFlag) {
        if (isFrontFace) backFaceColor else frontFaceColor
    }
    val duration = remember(freezeFlag) {
        if (isFrontFace) 2000 else 1000
    }
    val handleScratch = {
        scope.launch {
            if (isAnimating) return@launch
            isAnimating = true
            scratchProgress.snapTo(0f)
            if (isFrontFace) onFrontFaceClick() else onBackFaceClick()
            scratchProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = duration, easing = LinearEasing)
            )
            scratchProgress.snapTo(0f)
            isAnimating = false
            freezeFlag = !freezeFlag
        }
    }

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { if (!isAnimating) handleScratch() },
                onLongClick = onLongClick
            ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 10.dp,
            pressedElevation = 10.dp,
            focusedElevation = 10.dp,
            hoveredElevation = 10.dp,
            draggedElevation = 10.dp,
            disabledElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // 仅在动画期间开启离屏合成，避免全时段额外的绘制性能开销
                    if (isAnimating) {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                }
                .drawWithContent {
                    if (isAnimating) {
                        // 1. 绘制底层（新颜色）
                        drawRect(nextColor)

                        drawContext.canvas.withSaveLayer(
                            bounds = size.toRect(),
                            paint = layerPaint
                        ) {

                            drawRect(currentColor)

                            // 3. 计算蛇形刮除路径
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

                            // 4. 使用 DstOut 挖空当前图层（擦除顶层旧颜色）
                            drawPath(
                                path = eraseSegment,
                                color = Color.Black,
                                style = Stroke(
                                    width = gap * 2.0f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                ),
                                blendMode = BlendMode.DstOut
                            )
                        }


                        // 5. 绘制卡片内部文本/UI内容
                        drawContent()
                    } else {
                        // 未播放动画时，静态绘制当前颜色及卡片内容
                        drawRect(staticColor)
                        drawContent()
                    }
                }
        ) {
            content()
        }
    }
}