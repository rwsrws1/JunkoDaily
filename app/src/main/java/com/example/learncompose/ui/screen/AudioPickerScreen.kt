package com.example.learncompose.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun AudioPickerScreen() {

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        data class CommonItem(
            val id: Int,
            @DrawableRes val imageResId: Int,
            val contentDescription: String = ""
        )

        val screenWidth = LocalWindowInfo.current.containerDpSize.width

        val pageItems = remember {
            listOf(
                CommonItem(0, R.drawable.landscape1),
                CommonItem(1, R.drawable.landscape2),
                CommonItem(2, R.drawable.landscape3),
                CommonItem(3, R.drawable.landscape4),
                CommonItem(4, R.drawable.landscape5),
                CommonItem(5, R.drawable.landscape6),
                CommonItem(6, R.drawable.landscape7),
            )
        }

        val horizontalPadding = screenWidth * 0.05f
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
                    delay(2000.milliseconds) // 停顿 2 秒
                    // 带着动画丝滑地滑动到下一页
                    pagerState.animateScrollToPage(
                        page = pagerState.currentPage + 1,
                        animationSpec = tween(
                            durationMillis = 2000,
                            delayMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                pageSize = PageSize.Fill,
                contentPadding = PaddingValues(horizontal = horizontalPadding),
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

            // --- 上层：圆点指示器 ---
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = horizontalPadding + 16.dp,
                        bottom = 12.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(actualPageCount) { index ->
                    // 💡 技巧 5：指示器的高亮判断也需要取余
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

        val carouselItems = remember {
            listOf(
                CommonItem(0, R.drawable.girl1),
                CommonItem(1, R.drawable.girl2),
                CommonItem(2, R.drawable.girl3),
                CommonItem(3, R.drawable.girl4),
                CommonItem(4, R.drawable.girl5),
                CommonItem(5, R.drawable.girl1),
                CommonItem(6, R.drawable.girl2),
                CommonItem(7, R.drawable.girl3),
                CommonItem(8, R.drawable.girl4),
                CommonItem(9, R.drawable.girl5),
                CommonItem(10, R.drawable.girl1),
                CommonItem(11, R.drawable.girl2),
                CommonItem(12, R.drawable.girl3),
                CommonItem(13, R.drawable.girl4),
                CommonItem(14, R.drawable.girl5)
            )
        }
        val carouselState = rememberCarouselState { carouselItems.count() }
        HorizontalMultiBrowseCarousel(
            state = carouselState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp),
            preferredItemWidth = screenWidth * 0.4f,
            itemSpacing = 12.dp,
            contentPadding = PaddingValues(horizontal = 12.dp)
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

}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AudioPickerScreen()
}