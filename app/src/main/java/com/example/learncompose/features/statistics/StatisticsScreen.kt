package com.example.learncompose.features.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.learncompose.features.statistics.navigation.StatisticsNavKey
import com.example.learncompose.ui.components.ChartDemoScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(modifier: Modifier = Modifier) {
    val topTabs = listOf(StatisticsNavKey.Year, StatisticsNavKey.Month, StatisticsNavKey.Day)
    var selectedDestination by rememberSaveable { mutableIntStateOf(topTabs.indexOf(StatisticsNavKey.Month)) }

    Column(modifier = modifier) {
        PrimaryTabRow(selectedTabIndex = selectedDestination) {
            topTabs.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        selectedDestination = index
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
        ChartDemoScreen()
    }
}

@Preview
@Composable
private fun Preview() {
    StatisticsScreen()
}