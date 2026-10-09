package com.junko.junkodaily.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CurlingCard(
    aspectRatio: Float = 2f/3f,
    frontFaceColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    backFaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    shadowColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
    frontFaceContent: @Composable ColumnScope.() -> Unit = {},
) {
    var isCurled by rememberSaveable { mutableStateOf(false) }

    // 0f = 完全展开，1f = 折叠到最大程度
    val curlProgress by animateFloatAsState(
        targetValue = if (isCurled) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "curlAnimation"
    )

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .clickable(
                indication = null, // 禁用默认涟漪以突出折叠视觉
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isCurled = !isCurled
            }
            .pageCurlModifier(
                progress = curlProgress,
                backColor = backFaceColor,
                shadowColor = shadowColor
            ),
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(containerColor = frontFaceColor),
    ) {
        frontFaceContent()
    }
}

/**
 * 右下角斜向内折叠修饰符
 */
/**
 * 支持圆角卡牌的右下角斜向翻折修饰符
 *
 * @param progress 翻折动画进度 (0f..1f)
 * @param backColor 卡牌背面的颜色
 * @param shadowColor 阴影颜色
 * @param cornerRadius 卡片圆角半径（CardDefaults.shape 默认通常为 12.dp）
 */
private fun Modifier.pageCurlModifier(
    progress: Float,
    backColor: Color,
    shadowColor: Color,
    cornerRadius: Dp = 12.dp
): Modifier = this.drawWithContent {
    if (progress <= 0f) {
        drawContent()
        return@drawWithContent
    }

    val w = size.width
    val h = size.height
    val r = cornerRadius.toPx()

    // 最大折叠尺寸（可根据需要调整折角大小）
    val maxFold = w * 0.55f
    val foldSize = maxFold * progress

    // 翻折轴线与右边、底边的切点
    val ptRight = Offset(w, h - foldSize)
    val ptBottom = Offset(w - foldSize, h)

    // 1. 卡片主体裁剪：保留原本的圆角矩形轮廓，再减去右下角翻折区域
    val cardBaseRoundRect = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(0f, 0f, w, h),
                topLeft = CornerRadius(r, r),
                topRight = CornerRadius(r, r),
                bottomLeft = CornerRadius(r, r),
                bottomRight = CornerRadius(r, r)
            )
        )
    }

    // 掀开区域的切角（从 ptRight 切到 ptBottom）
    val cutAwayPath = Path().apply {
        moveTo(ptRight.x, ptRight.y)
        lineTo(w, h - foldSize)
        lineTo(w, h)
        lineTo(w - foldSize, h)
        lineTo(ptBottom.x, ptBottom.y)
        close()
    }

    // 差集操作：卡牌主体只保留未被切掉的部分
    val remainingCardPath = Path.combine(
        operation = PathOperation.Difference,
        path1 = cardBaseRoundRect,
        path2 = cutAwayPath
    )

    clipPath(remainingCardPath) {
        this@drawWithContent.drawContent()
    }

    // 2. 绘制卡片原位置的右下角阴影（严格限制在圆角范围内）
    val shadowAreaPath = Path().apply {
        moveTo(ptRight.x, ptRight.y)
        lineTo(ptBottom.x, ptBottom.y)
        lineTo(w, h)
        close()
    }
    val clippedShadowPath = Path.combine(
        operation = PathOperation.Intersect,
        path1 = cardBaseRoundRect,
        path2 = shadowAreaPath
    )

    val midFold = Offset((ptRight.x + ptBottom.x) / 2f, (ptRight.y + ptBottom.y) / 2f)
    drawPath(
        path = clippedShadowPath,
        brush = Brush.linearGradient(
            colors = listOf(shadowColor, Color.Transparent),
            start = midFold,
            end = Offset(w, h)
        )
    )

    // 3. 绘制翻折上来的圆角三角形页片
    // 翻折后的圆角中心与弧线镜像计算
    val foldedCornerPath = Path().apply {
        val arcR = r.coerceAtMost(foldSize / 2f)
        val foldPeak = Offset(w - foldSize, h - foldSize)

        moveTo(ptRight.x, ptRight.y)
        // 从右切点向折回的尖峰处画线，在接近尖端处转为圆角弧线
        lineTo(foldPeak.x + arcR, foldPeak.y)
        // 翻折后的圆弧（平滑过渡尖端）
        quadraticTo(
            foldPeak.x, foldPeak.y,
            foldPeak.x, foldPeak.y + arcR
        )
        // 连向底侧切点
        lineTo(ptBottom.x, ptBottom.y)
        close()
    }

    // 绘制翻折上来的背面底色
    drawPath(
        path = foldedCornerPath,
        color = backColor
    )

    // 4. 翻折纸片的高光与折痕渐变立体感
    val foldPeak = Offset(w - foldSize, h - foldSize)
    drawPath(
        path = foldedCornerPath,
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = 0.9f), Color.Transparent),
            start = foldPeak,
            end = midFold
        )
    )
}