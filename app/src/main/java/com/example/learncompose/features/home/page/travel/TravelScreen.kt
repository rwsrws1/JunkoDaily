package com.example.learncompose.features.home.page.travel

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionDefaults
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan // 记得导入这个
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.request.crossfade
import com.example.learncompose.R
import com.example.learncompose.navigation.AppNavKey
import com.example.learncompose.navigation.LocalAppNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

// === 1. 状态和数据定义移到最外层 ===
data class CommonItem(
    val id: Int,
    val imageResId: Int,
    val contentDescription: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelScreen(
    onNavigateToLoading: () -> Unit = {}
) {
    val navigatorTo = LocalAppNavigator.current

    val screenWidth = LocalWindowInfo.current.containerDpSize.width
    val pageSpacing = screenWidth * 0.05f
    val horizontalPadding = 12.dp

    val staggeredGridState = rememberLazyStaggeredGridState()

    val pageItems = remember {
        listOf(
            CommonItem(0, R.drawable.landscape1),
            CommonItem(1, R.drawable.landscape4),
            CommonItem(2, R.drawable.landscape2),
            CommonItem(3, R.drawable.landscape5),
            CommonItem(4, R.drawable.landscape3),
            CommonItem(5, R.drawable.landscape6),
            CommonItem(6, R.drawable.landscape7),
        )
    }

    val actualPageCount = pageItems.size
    val startIndex = Int.MAX_VALUE / 2
    val initialPage = startIndex - (startIndex % actualPageCount)

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(isDragged) {
        if (!isDragged) {
            while (true) {
                delay(2000.milliseconds)
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    val carouselItems = remember {
        listOf(
            CommonItem(0, R.drawable.girl1),
            CommonItem(1, R.drawable.girl2),
            CommonItem(2, R.drawable.girl3),
            CommonItem(3, R.drawable.girl4),
            CommonItem(4, R.drawable.girl5),
            CommonItem(5, R.drawable.girl6),
            CommonItem(6, R.drawable.girl7)
        )
    }

    val carouselState = rememberCarouselState(itemCount = {carouselItems.count()})

    val gridItems = remember {
        listOf(
            CommonItem(0, R.drawable.dali1),
            CommonItem(1, R.drawable.dali2),
            CommonItem(2, R.drawable.dali3),
            CommonItem(3, R.drawable.dali4),
            CommonItem(4, R.drawable.dali5),
            CommonItem(5, R.drawable.dali6),
            CommonItem(6, R.drawable.dali7),
            CommonItem(7, R.drawable.dali8),
            CommonItem(8, R.drawable.dali9),
            CommonItem(9, R.drawable.dali10),
            CommonItem(10, R.drawable.dali11),
            CommonItem(11, R.drawable.dali12),
            CommonItem(12, R.drawable.dali13),
            CommonItem(13, R.drawable.dali14),
            CommonItem(14, R.drawable.dali15),
            CommonItem(15, R.drawable.dali16),
            CommonItem(16, R.drawable.dali17),
            CommonItem(17, R.drawable.dali18),
        )
    }

    // 1. 获取系统默认的滑动物理特性
    val defaultFlingBehavior = ScrollableDefaults.flingBehavior()

    // 2. 创建一个减速版的 FlingBehavior
    val slowFlingBehavior = remember(defaultFlingBehavior) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                // 核心逻辑：将初始速度乘以一个小于 1 的阻尼系数
                // 例如 0.4f 表示把甩动速度削弱到原来的 40%
                // 这个值你可以根据实际手感进行微调（0.1f ~ 0.9f）
                val dampedVelocity = initialVelocity * 0.6f

                // 将减弱后的速度交给系统默认的处理机制
                return with(defaultFlingBehavior) {
                    performFling(dampedVelocity)
                }
            }
        }
    }

    var isShowDetail by rememberSaveable { mutableStateOf(false) }
    var detailPictureId by rememberSaveable { mutableIntStateOf(0) }
    val customBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
        // 你可以使用 tween(固定时间) 也可以使用 spring(弹性)
        tween(durationMillis = 600, easing = FastOutSlowInEasing)
    }

//    val coroutineScope = rememberCoroutineScope()
//    val stretchOffset = remember { Animatable(0f) }
//    val stretchScrollConnection = remember {
//        object : NestedScrollConnection {
//            override fun onPostScroll(
//                consumed: Offset,
//                available: Offset,
//                source: NestedScrollSource
//            ): Offset {
//                if (source == NestedScrollSource.UserInput && available.x != 0f) {
//                    val damping = 0.1f
//                    val newTarget = stretchOffset.value + available.x * damping
//                    val clamped = newTarget.coerceIn(-100f, 100f)
//                    coroutineScope.launch {
//                        stretchOffset.snapTo(clamped)
//                    }
//                    return Offset(available.x, 0f)
//                }
//                return Offset.Zero
//            }
//
//            override suspend fun onPostFling(
//                consumed: Velocity,
//                available: Velocity
//            ): Velocity {
//                stretchOffset.animateTo(
//                    targetValue = 0f,
//                    animationSpec = spring(
//                        dampingRatio = Spring.DampingRatioNoBouncy,
//                        stiffness = Spring.StiffnessMedium
//                    )
//                )
//                return Velocity.Zero
//            }
//        }
//    }
//    val currentOffset = stretchOffset.value
//    val scaleFactor = 1f + (abs(currentOffset) / 2000f)
//    val transformOrigin = if (currentOffset >= 0f) {
//        TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0.5f)
//    } else {
//        TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0.5f)
//    }


    val stopScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return if (available.x < 0f) {
                    Offset(x = available.x, y = 0f)
                } else {
                    Offset.Zero
                }
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                return if (available.x < 0f) {
                    Velocity(x = available.x, y = 0f)
                } else {
                    Velocity.Zero
                }
            }
        }
    }

    SharedTransitionLayout(

    ) {
        AnimatedContent(
            targetState = isShowDetail,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith ExitTransition.None
            },
        ) { isShow ->
            if (isShow) {
                Column(
                    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = {
                                isShowDetail = false
                            }
                        )
                ) {
                    Spacer(Modifier.weight(1F))
                    Image(
                        painterResource(detailPictureId), null,
                        Modifier.fillMaxWidth().sharedBounds(
                            sharedContentState = rememberSharedContentState("detail_element${detailPictureId}"),
                            animatedVisibilityScope = this@AnimatedContent,
                            boundsTransform = customBoundsTransform,
                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.Crop),
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(0.dp))
                        ).clip(RoundedCornerShape(0.dp))
                            .clickable(
                                interactionSource = null,
                                indication = null,
                                onClick = {}
                            ),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "测试测试",
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.weight(1F))
                }
            } else {
                LazyVerticalStaggeredGrid(
                    state = staggeredGridState,
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                    columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(horizontalPadding),
                    verticalItemSpacing = 12.dp,
                    flingBehavior = slowFlingBehavior
                ) {
                    // --- 第一部分：景点标题 (跨满整行) ---
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = horizontalPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painter = painterResource(R.drawable.landscape_2_24px), null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "风景", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    // --- 第二部分：顶部无限轮播 Pager (跨满整行) ---
                    item(span = StaggeredGridItemSpan.FullLine) {
                        // ... 你原本的 HorizontalPager 和 指示器 Box 代码 ...
                        Box(modifier = Modifier.fillMaxWidth()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth(),
                                pageSize = PageSize.Fill,
                                pageSpacing = pageSpacing,
                                contentPadding = PaddingValues(horizontal = horizontalPadding)
                            ) { page ->
                                val realIndex = page % actualPageCount
                                val item = pageItems[realIndex]
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .clickable(
                                            onClick = {
                                                onNavigateToLoading()
                                            }
                                        )
                                ) {
                                    AsyncImageOptimize(
                                        model = item.imageResId,
                                        modifier = Modifier.fillMaxWidth().aspectRatio(3f / 2f),
                                        contentScale = ContentScale.FillWidth,
                                        disableCachePolicy = true
                                    )
                                }
                            }

                            // 指示器圆点
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 16.dp, bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(actualPageCount) { index ->
                                    val currentRealPage = pagerState.currentPage % actualPageCount
                                    val isSelected = currentRealPage == index
                                    Box(
                                        modifier = Modifier
                                            .size(if (isSelected) 8.dp else 6.dp)
                                            .background(
                                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // --- 第三部分：人像标题 (跨满整行) ---
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = horizontalPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painter = painterResource(R.drawable.nature_people_24px), null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "人像", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    // --- 第四部分：横向画廊 Carousel (跨满整行) ---
                    item(span = StaggeredGridItemSpan.FullLine) {

//            HorizontalUncontainedCarousel(
//                state = carouselState,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//                    .nestedScroll(stopScrollConnection),
//                itemWidth = screenWidth * 0.4f,
//                itemSpacing = 12.dp,
//                contentPadding = PaddingValues(horizontal = horizontalPadding)
//            ) { i ->
//                val item = carouselItems[i]
//                AsyncImageOptimize(
//                    model = item.imageResId,
//                    modifier = Modifier
//                        .height(205.dp)
//                        .maskClip(MaterialTheme.shapes.extraLarge),
//                )
//            }

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .nestedScroll(stopScrollConnection)
//                                .nestedScroll(stretchScrollConnection)
//                                .graphicsLayer {
//                                    scaleX = scaleFactor
//                                    this.transformOrigin = transformOrigin
//                                }
                            ,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = horizontalPadding)
                        ) {
                            items(count = carouselItems.count(), key = { it }) { index ->
                                AsyncImageOptimize(
                                    model = carouselItems[index].imageResId,
                                    modifier = Modifier
                                        .height(205.dp)
                                        .aspectRatio(1f / 1.4f)
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .clickable(
                                            onClick = {
                                                navigatorTo(AppNavKey.Loading)
                                            }
                                        ),
                                )
                            }
                        }

                    }

                    // --- 第五部分：网格列表的标题 (跨满整行) ---
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = horizontalPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painter = painterResource(R.drawable.distance_24px), null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "地点", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    // --- 第六部分：真正的网格内容 ---
                    itemsIndexed(
                        gridItems
                    ) { index, item ->
                        val isLeftColumn = index % 2 == 0
                        val startPad = if (isLeftColumn) horizontalPadding else 0.dp
                        val endPad = if (isLeftColumn) 0.dp else horizontalPadding
                        AsyncImageOptimize(
                            model = item.imageResId,
                            modifier = Modifier
                                .padding(start = startPad, end = endPad)
                                .aspectRatio(1f / 1f)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState("detail_element${item.imageResId}"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = customBoundsTransform,
                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.Crop),
                                    clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.extraLarge)
                                )
                                .clip(MaterialTheme.shapes.extraLarge)
                                .clickable(
                                    onClick = {
                                        detailPictureId = item.imageResId
                                        isShowDetail = true
                                    }
                                )
                        )
                    }
                }

            }

        }

    }

    // === 2. 使用 LazyVerticalGrid 作为整个页面的根节点 ===
    // 删除了 Column 和 verticalScroll
}

@Composable
fun AsyncImageOptimize(
    model: Any?,
    modifier: Modifier = Modifier,
    targetSizePx: Int? = 360, // 针对列表缩略图的像素大小，默认 360x360
    contentScale: ContentScale = ContentScale.Crop,
    disableCachePolicy: Boolean = false
) {
    val context = LocalContext.current
    val imageRequest = remember(model, targetSizePx) {
        ImageRequest.Builder(context)
            .data(model)
            .apply {
                targetSizePx?.let {
                    size(targetSizePx, targetSizePx)
                }
            }
            .apply {
                if (disableCachePolicy) {
                    diskCachePolicy(CachePolicy.ENABLED)
                    memoryCachePolicy(CachePolicy.DISABLED)
                }
            }
            .bitmapConfig(Bitmap.Config.RGB_565) // 极省内存的格式
            .allowHardware(true) // 优先使用硬件内存
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}

// 定义一个突破父容器 Padding 的 Modifier
fun Modifier.ignoreParentPadding(horizontalPadding: Dp) = this.layout { measurable, constraints ->
    val paddingPx = horizontalPadding.roundToPx()

    // 1. 强行放大测量约束，把被父容器扣掉的宽度（左右两边）加回来
    val expandedConstraints = constraints.copy(
        maxWidth = constraints.maxWidth + paddingPx * 2
    )
    val placeable = measurable.measure(expandedConstraints)

    // 2. 关键修改：向父容器报告 constraints.maxWidth (原始的受限宽度)
    // 这样父容器仍然觉得你在规规矩矩地待在原位，不会改变整体居中对齐方式
    layout(constraints.maxWidth, placeable.height) {
        // 3. 向左偏移，抵消父容器左侧的 padding，使得组件真正贴到屏幕左边缘
        placeable.place(-paddingPx, 0)
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TravelScreen()
}