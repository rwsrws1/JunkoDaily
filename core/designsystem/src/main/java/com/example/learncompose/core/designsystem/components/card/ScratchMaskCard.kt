package com.example.learncompose.core.designsystem.components.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.learncompose.core.designsystem.components.FloatAnimatableSaver
import kotlinx.coroutines.launch

@Composable
fun ScratchMaskCard(
    cardId: Long = 0,
    aspectRatio: Float = 2f / 3f,
    frontFaceColor: Color = Color.Unspecified,
    backFaceColor: Color = Color.Unspecified,
    onFrontFaceClick: () -> Unit = {},
    onBackFaceClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    isFrontFace: Boolean = true,
    content: @Composable () -> Unit = {}
) {




// 记住上一轮的值
    var lastCardId by remember { mutableStateOf(cardId) }
    var lastAspectRatio by remember { mutableStateOf(aspectRatio) }
    var lastFrontColor by remember { mutableStateOf(frontFaceColor) }
    var lastBackColor by remember { mutableStateOf(backFaceColor) }
    var lastIsFrontFace by remember { mutableStateOf(isFrontFace) }
    var lastOnFrontClick by remember { mutableStateOf(onFrontFaceClick) }
    var lastOnBackClick by remember { mutableStateOf(onBackFaceClick) }
    var lastOnLongClick by remember { mutableStateOf(onLongClick) }
    var lastContent by remember { mutableStateOf(content) }

    SideEffect {
        val reasons = mutableListOf<String>()

        if (lastCardId != cardId) reasons.add("cardId: $lastCardId -> $cardId")
        if (lastAspectRatio != aspectRatio) reasons.add("aspectRatio: $lastAspectRatio -> $aspectRatio")
        if (lastFrontColor != frontFaceColor) reasons.add("frontFaceColor: $lastFrontColor -> $frontFaceColor")
        if (lastBackColor != backFaceColor) reasons.add("backFaceColor: $lastBackColor -> $backFaceColor")
        if (lastIsFrontFace != isFrontFace) reasons.add("isFrontFace: $lastIsFrontFace -> $isFrontFace")

        // 引用比较：判断 Lambda 函数对象是否重新生成了
        if (lastOnFrontClick !== onFrontFaceClick) reasons.add("onFrontFaceClick (Lambda 引用改变)")
        if (lastOnBackClick !== onBackFaceClick) reasons.add("onBackFaceClick (Lambda 引用改变)")
        if (lastOnLongClick !== onLongClick) reasons.add("onLongClick (Lambda 引用改变)")
        if (lastContent !== content) reasons.add("content (Lambda 引用改变)")

        if (reasons.isNotEmpty()) {
            println("ScratchMaskCard [$cardId] 重组原因: ${reasons.joinToString(", ")}")
        } else {
            println("ScratchMaskCard [$cardId] 发生重组，但所有已知参数值/引用均无明显变化 (可能是无状态更新引起的强制重组)")
        }

        // 更新上一轮的值
        lastCardId = cardId
        lastAspectRatio = aspectRatio
        lastFrontColor = frontFaceColor
        lastBackColor = backFaceColor
        lastIsFrontFace = isFrontFace
        lastOnFrontClick = onFrontFaceClick
        lastOnBackClick = onBackFaceClick
        lastOnLongClick = onLongClick
        lastContent = content
    }








    val scope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false)  }
    val scratchProgress = remember { Animatable(0f) }
    val layerPaint = remember { Paint() }
    val staticColor = if (isFrontFace) frontFaceColor else backFaceColor

    var freezeFlag by remember { mutableStateOf(true)  }
    println("cardId:$cardId, isFrontFace:$isFrontFace, isAnimating:$isAnimating")
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
            )
            .border(2.dp, color = MaterialTheme.colorScheme.onSurface, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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

                        drawContext.canvas.withSaveLayer(bounds = size.toRect(), paint = layerPaint) {

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