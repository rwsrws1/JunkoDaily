package com.example.learncompose.screen

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StackRecentScreen(
    modifier: Modifier = Modifier,
    cardCount: Int = 8
) {
    val pagerState = rememberPagerState(pageCount = { cardCount })

    val splineDecay = rememberSplineBasedDecay<Float>()

    val customDecayFlingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(8), // 允许连滑多页
        decayAnimationSpec = splineDecay,
        snapAnimationSpec = tween(durationMillis = 200) // 使用线性/缓动时间控制对齐时长
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        // 给左右留出内边距，让相邻卡片可见
        contentPadding = PaddingValues(horizontal = 64.dp),
        pageSpacing = 0.dp,
        flingBehavior = customDecayFlingBehavior
    ) { page ->
        // 计算当前页与活跃页面的相对偏移量：当前页为 0，左侧为正数，右侧为负数
        val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)

        Card(
            modifier = Modifier
                .height(200.dp)
                .aspectRatio(1.5f/1f)
                // 1. zIndex：确保前方的卡片始终覆盖在后方堆叠的卡片之上
                .zIndex(if (pageOffset > 0) 100f - pageOffset else 100f + pageOffset)
                .graphicsLayer {
                    val absOffset = pageOffset.absoluteValue.coerceIn(0f, 2f)

                    // 2. 缩放变换：非聚焦卡片按比例缩小
                    val scale = lerp(
                        start = 1f,
                        stop = 0.82f,
                        fraction = absOffset.coerceIn(0f, 1f)
                    )
                    scaleX = scale
                    scaleY = scale

                    // 3. 透明度渐变：离开屏幕中心的卡片淡出
                    alpha = lerp(
                        start = 1f,
                        stop = 0.4f,
                        fraction = absOffset.coerceIn(0f, 1f)
                    )

                    // 4. 位移抵消（实现堆叠收拢而不是直接滑出屏幕）
                    if (pageOffset > 0) {
                        // 左侧已滑过的卡片：抵抗部分位移，让它们在左侧挤压层叠
                        translationX = pageOffset * size.width * 0.7f
                    } else {
                        // 右侧待滑入的卡片：微调间距
                        translationX = pageOffset * size.width * 0.7f
                    }
                },
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2C2C2C)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "App Task #${page + 1}",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    StackRecentScreen()
}