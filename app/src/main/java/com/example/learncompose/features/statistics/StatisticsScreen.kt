package com.example.learncompose.features.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learncompose.R
import com.example.learncompose.features.statistics.navigation.StatisticsNavKey
import com.example.learncompose.ui.components.ChartDemoScreen
import com.example.learncompose.ui.components.CommonScreen
import com.example.learncompose.ui.screen.DrawingBoardScreen
import com.example.learncompose.ui.screen.MediaPickerScreen
import com.example.learncompose.ui.screen.ScratchCardScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(modifier: Modifier = Modifier) {
    val topTabs = listOf(StatisticsNavKey.Year, StatisticsNavKey.Month, StatisticsNavKey.Day)
    var selectedDestination by rememberSaveable { mutableIntStateOf(topTabs.indexOf(StatisticsNavKey.Month)) }
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
                        Text(
                            text = when (destination) {
                                is StatisticsNavKey.Year -> "年"
                                is StatisticsNavKey.Month -> "月"
                                is StatisticsNavKey.Day -> "日"
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pageState
        ) { page ->
            when (page) {
                0 -> MediaPickerScreen()
                1 -> ChartDemoScreen()
                2 -> CommonScreen()
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    StatisticsScreen()
}