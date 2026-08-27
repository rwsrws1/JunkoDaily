package com.example.learncompose.features.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.learncompose.data.room.UserScreen
import com.example.learncompose.features.home.navigation.HomeNavKey
import com.example.learncompose.ui.screen.FeedScreen
import com.example.learncompose.features.home.page.travel.TravelScreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val topTabs = listOf(HomeNavKey.Travel, HomeNavKey.Chart, HomeNavKey.MainIcon)
    var selectedDestination by rememberSaveable { mutableIntStateOf(topTabs.indexOf(HomeNavKey.Travel)) }
    val pageState = rememberPagerState(initialPage = selectedDestination, pageCount = {topTabs.size})
    val scop = rememberCoroutineScope()

    Column(modifier = modifier) {
        PrimaryTabRow(selectedTabIndex = pageState.currentPage) {
            topTabs.forEachIndexed { index, destination ->
                Tab(
                    selected = pageState.currentPage == index,
                    onClick = {
                        selectedDestination = index
                        scop.launch {
                            pageState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = when (destination) {
                                    is HomeNavKey.Travel -> "旅行"
                                    is HomeNavKey.Chart -> "统计"
                                    is HomeNavKey.MainIcon -> "列表"
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                )
            }
        }
        HorizontalPager(
            state = pageState,
            beyondViewportPageCount = 0
        ) { page ->
            when (page) {
                0 -> TravelScreen()
                1 -> UserScreen()
                2 -> FeedScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    HomeScreen()
}