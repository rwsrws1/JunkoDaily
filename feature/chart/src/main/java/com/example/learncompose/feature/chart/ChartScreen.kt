package com.example.learncompose.feature.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learncompose.core.designsystem.components.TopBarPrimary
import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardsAndLogs
import com.example.learncompose.feature.routine.chart.ChartContract
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

val RoutineCard.composeColor: Color
    get() = Color(this.cardColor)

@Composable
fun ChartViewModelScreen(modifier: Modifier = Modifier, viewModel: ChartViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ChartScreen(modifier, uiState)
}

@Composable
fun ChartScreen(modifier: Modifier = Modifier, uiState: ChartContract.UiState = ChartContract.UiState()) {
    val cardsAndLogs = uiState.cardsAndLogs

    val today = LocalDate.now()
    val pageCount = 12
    val pagerState = rememberPagerState(
        initialPage = pageCount,
        pageCount = { pageCount }
    )

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM")



    Box(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background))
    {
        Column(Modifier.fillMaxSize()) {
            TopBarPrimary()
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                beyondViewportPageCount = 0,
                pageSpacing = 10.dp,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val currentDay = today.minusMonths((pageCount - page - 1).toLong())
                val yearMonthStr = currentDay.format(formatter)
                Column(Modifier.fillMaxSize()) {
                    Text("$yearMonthStr", Modifier.align(Alignment.CenterHorizontally), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(10.dp))
                    ChartCard(cardsAndLogs, currentDay.year, currentDay.monthValue)
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    cardsAndLogs: List<RoutineCardsAndLogs>,
    currentYear: Int,
    currentMonth: Int,
) {
    val daysInMonths = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(150.dp),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(count = cardsAndLogs.size, key = { it }) { index ->
            val completedDays = cardsAndLogs[index].logs.filter {
                it.recordDate?.year == currentYear
            }.filter {
                it.recordDate?.monthValue == currentMonth
            }.filter {
                it.isCompleted
            }.map {
                it.recordDate?.dayOfMonth
            }
            Card(
                Modifier
                    .aspectRatio(1f / 1f)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.medium
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Spacer(Modifier.height(10.dp))
                Text(
                    cardsAndLogs[index].card.cardText,
                    Modifier.align(Alignment.CenterHorizontally)
                )
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(7),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    item {
                        Box(
                            Modifier
                                .aspectRatio(1f / 1f)
                                .background(
                                    Color.Transparent
                                )
                        )
                    }
                    item {
                        Box(
                            Modifier
                                .aspectRatio(1f / 1f)
                                .background(
                                    Color.Transparent
                                )
                        )
                    }
                    items(count = daysInMonths, key = { it }) { day ->
                        DayBox(
                            color = if (completedDays.contains(day)) cardsAndLogs[index].card.composeColor
                            else MaterialTheme.colorScheme.surfaceContainerHighest,
                            number = day + 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayBox(modifier: Modifier = Modifier, color: Color, number: Int) {
    Box(modifier
        .aspectRatio(1f / 1f)
        .clip(MaterialTheme.shapes.extraSmall)
        .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text("$number", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.surfaceContainerLowest)
    }
}

@Preview
@Composable
private fun Preview() {
    ChartScreen()
}