package com.example.learncompose.feature.note

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learncompose.core.designsystem.theme.AppTheme
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class GariItem(
    val id: Int = 0,
    val text: String = "",
    val color: Long = 0xFF9FEFFF,
)

val FloatAnimatableSaver = Saver<Animatable<Float, AnimationVector1D>, Float>(
    save = { it.value }, // 保存时，只提取当前的 Float 值
    restore = { Animatable(it) } // 恢复时，用保存的 Float 值重新创建 Animatable
)

@Composable
fun NoteScreen(modifier: Modifier = Modifier, onClick: () -> Unit = {}, toExperiment: () -> Unit = {}) {

    val gridItems = remember {
        List(50) { index ->
            GariItem(id = index, text = "收到就哦啊世界第哦啊十九大收到就哦啊世界第哦啊十九大")
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(50.dp))
            StaggeredCardGrid(gridItems, onClick, toExperiment)
        }
    }
}

@Composable
fun StaggeredCardGrid(gridItems: List<GariItem>, onClick: () -> Unit = {}, toExperiment: () -> Unit = {}) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(70.dp),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = gridItems, key = { item -> item.id }) { item ->
            FlipCard(item = item)
        }
        item(span = { GridItemSpan(maxLineSpan) }, key = "button_Button_1") {
            Button(onClick = toExperiment) {
                Text("go to experiment")
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }, key = "button_Button_2") {
            Button(onClick = onClick) {
                Text("go to next page")
            }
        }
    }
}

@Composable
fun FlipCard(item: GariItem) {
    var isFlip by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val cardColor = MaterialTheme.colorScheme.secondaryContainer
    val backFaceColor = MaterialTheme.colorScheme.surfaceVariant

    // 动画状态：旋转角度 (0f ~ 180f) 与 缩放比例 (1f -> 1.08f -> 1f)
    val rotationY = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }
    val scale = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(1f) }

    val handleFlip = {
        scope.launch {
            // 1. 抬起：轻微放大，模拟离开桌面
            scale.animateTo(
                targetValue = 1.08f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )

            // 2. 翻转：沿 Y 轴旋转 180°
            val targetRotation = if (!isFlip) 180f else 0f
            rotationY.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
            isFlip = !isFlip

            // 3. 落下：恢复原始大小，模拟落回桌面
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
        }
    }

    // 旋转超过 90 度时展示背面
    val isShowingBack = rotationY.value > 90f

    Card(
        modifier = Modifier
            .aspectRatio(2f / 3f)
            .graphicsLayer {
                this.scaleX = scale.value
                this.scaleY = scale.value
                this.rotationY = rotationY.value
                // 增加 3D 摄像机视距，防止翻转时透视失真严重
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 去除原生水波纹，保持翻牌质感
            ) {
                if (!scale.isRunning && !rotationY.isRunning) {
                    handleFlip()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isShowingBack) backFaceColor else cardColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 当翻到背面时，将内部内容再沿 Y 轴翻转 180°，防止文字/图标镜像倒置
                .graphicsLayer {
                    if (isShowingBack) {
                        this.rotationY = 180f
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column {
                if (isShowingBack) {
                    Text(text = "背面")
                } else {
                    Text(text = "${item.id}", modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = item.text,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 8.sp,
                            maxFontSize = 14.sp,
                            stepSize = 0.5.sp
                        ),
//                        MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CurlingCard(item: GariItem) {
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

    val cardColor = MaterialTheme.colorScheme.secondaryContainer
    val backFaceColor = MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier
            .aspectRatio(2f / 3f)
            .clickable(
                indication = null, // 禁用默认涟漪以突出折叠视觉
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isCurled = !isCurled
            }
            .pageCurlModifier(
                progress = curlProgress,
                backColor = backFaceColor,
                shadowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            ),
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(containerColor = cardColor),
    ) {
        Text(text = "${item.id}", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.weight(1f))
        Text(
            text = item.text,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8F),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.weight(1f))
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
fun Modifier.pageCurlModifier(
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



@Preview
@Composable
private fun Preview() {
    AppTheme {
        NoteScreen()
    }
}