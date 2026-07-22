package com.example.learncompose.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan // 记得导入这个
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learncompose.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelScreen() {
    // === 1. 状态和数据定义移到最外层 ===
    data class CommonItem(
        val id: Int,
        val imageResId: Int,
        val contentDescription: String = ""
    )

    val screenWidth = LocalWindowInfo.current.containerDpSize.width
    val horizontalPadding = screenWidth * 0.05f

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
    val carouselState = rememberCarouselState { carouselItems.count() }

    val stopPagerScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                return Offset(x = available.x, y = 0f)
            }
        }
    }

    val gridItems = remember {
        listOf(
            CommonItem(0, R.drawable.dali1),
            CommonItem(1, R.drawable.dali2),
            CommonItem(2, R.drawable.dali3),
            CommonItem(3, R.drawable.dali4),
            CommonItem(4, R.drawable.dali5),
            CommonItem(5, R.drawable.dali6),
            CommonItem(6, R.drawable.dali7),
            CommonItem(6, R.drawable.dali8),
            CommonItem(6, R.drawable.dali9),
            CommonItem(6, R.drawable.dali10),
            CommonItem(6, R.drawable.dali11),
            CommonItem(6, R.drawable.dali12),
            CommonItem(6, R.drawable.dali13),
            CommonItem(6, R.drawable.dali14),
            CommonItem(6, R.drawable.dali15),
            CommonItem(6, R.drawable.dali16),
            CommonItem(6, R.drawable.dali17),
            CommonItem(6, R.drawable.dali18),
        )
    }

    // === 2. 使用 LazyVerticalGrid 作为整个页面的根节点 ===
    // 删除了 Column 和 verticalScroll
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        // 为了不让 Pager 被挤压，我们把 padding 设在底部和左右
        contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // --- 第一部分：景点标题 (跨满整行) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp), // 水平 Padding 已经由 Grid 的 contentPadding 提供了
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(R.drawable.landscape_2_24px), null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "风景", style = MaterialTheme.typography.titleMedium)
            }
        }

        // --- 第二部分：顶部无限轮播 Pager (跨满整行) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            // ... 你原本的 HorizontalPager 和 指示器 Box 代码 ...
            Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    pageSize = PageSize.Fill,
                    pageSpacing = horizontalPadding
                ) { page ->
                    val realIndex = page % actualPageCount
                    val item = pageItems[realIndex]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.extraLarge)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = painterResource(id = item.imageResId),
                            contentDescription = item.contentDescription,
                            contentScale = ContentScale.FillWidth
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
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(R.drawable.nature_people_24px), null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "人像", style = MaterialTheme.typography.titleMedium)
            }
        }

        // --- 第四部分：横向画廊 Carousel (跨满整行) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            HorizontalUncontainedCarousel(
                state = carouselState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .nestedScroll(stopPagerScrollConnection),
                itemWidth = screenWidth * 0.4f,
                itemSpacing = 12.dp,
                contentPadding = PaddingValues(horizontal = 0.dp) // Grid 外层已经有 16dp 了
            ) { i ->
                val item = carouselItems[i]
                Image(
                    modifier = Modifier
                        .height(205.dp)
                        .maskClip(MaterialTheme.shapes.extraLarge),
                    painter = painterResource(id = item.imageResId),
                    contentDescription = item.contentDescription,
                    contentScale = ContentScale.Crop
                )
            }
        }

        // --- 第五部分：网格列表的标题 (跨满整行) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(R.drawable.distance_24px), null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "地点", style = MaterialTheme.typography.titleMedium)
            }
        }

        // --- 第六部分：真正的网格内容 ---
        items(gridItems) {
            Image(
                modifier = Modifier
                    .aspectRatio(1f / 1f)
                    .clip(MaterialTheme.shapes.extraLarge),
                painter = painterResource(it.imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop // 推荐使用 Crop 让网格图片填满
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TravelScreen()
}