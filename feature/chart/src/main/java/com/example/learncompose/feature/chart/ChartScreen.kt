package com.example.learncompose.feature.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.remember
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

private val RoutineCard.composeColor: Color
    get() = Color(this.cardColor)

private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")

@Composable
fun ChartViewModelScreen(modifier: Modifier = Modifier, viewModel: ChartViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ChartScreen(modifier, uiState)
}

@Composable
fun ChartScreen(modifier: Modifier = Modifier, uiState: ChartContract.UiState = ChartContract.UiState()) {
    val cardsAndLogs = uiState.cardsAndLogs

    val today = LocalDate.now()
    val initialPage = 11
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { initialPage + 1 }
    )

    Box(modifier
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
                val currentDay = remember(page, today) {
                    today.minusMonths((initialPage - page).toLong())
                }
                val yearMonthStr = currentDay.format(YEAR_MONTH_FORMATTER)
                Column(Modifier.fillMaxSize()) {
                    Text("$yearMonthStr", Modifier.align(Alignment.CenterHorizontally), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(10.dp))
                    ChartPage(cardsAndLogs = cardsAndLogs, currentYear = currentDay.year, currentMonth = currentDay.monthValue)
                }
            }
        }
    }
}

@Composable
private fun ChartPage(
    modifier: Modifier = Modifier,
    cardsAndLogs: List<RoutineCardsAndLogs>,
    currentYear: Int,
    currentMonth: Int,
) {
    val daysInMonths = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(150.dp),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = cardsAndLogs, key = { it.card.id }) { item ->
//            val completedDays = cardsAndLogs[index].logs.filter {
//                it.recordDate?.year == currentYear
//            }.filter {
//                it.recordDate?.monthValue == currentMonth
//            }.filter {
//                it.isCompleted
//            }.map {
//                it.recordDate?.dayOfMonth
//            }
            val completedDays = remember(item.logs, currentYear, currentMonth) {
                item.logs.asSequence()
                    .filter { it.isCompleted && it.recordDate != null }
                    .filter { it.recordDate!!.year == currentYear && it.recordDate!!.monthValue == currentMonth }
                    .mapTo(HashSet()) { it.recordDate!!.dayOfMonth }
            }
            ChartCard(
                cardAndLog = item,
                daysInMonths = daysInMonths,
                completedDays = completedDays
            )
        }
    }
}

@Composable
private fun ChartCard(
    modifier: Modifier = Modifier,
    cardAndLog: RoutineCardsAndLogs,
    daysInMonths: Int,
    completedDays: Set<Int>
) {
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
            cardAndLog.card.cardText,
            Modifier.align(Alignment.CenterHorizontally)
        )


//        LazyVerticalGrid(
//            modifier = Modifier.fillMaxSize(),
//            columns = GridCells.Fixed(7),
//            contentPadding = PaddingValues(10.dp),
//            verticalArrangement = Arrangement.spacedBy(3.dp),
//            horizontalArrangement = Arrangement.spacedBy(3.dp)
//        ) {
//            item(key = "box_a") {
//                Box(
//                    Modifier
//                        .aspectRatio(1f / 1f)
//                        .background(
//                            Color.Transparent
//                        )
//                )
//            }
//            item(key = "box_b") {
//                Box(
//                    Modifier
//                        .aspectRatio(1f / 1f)
//                        .background(
//                            Color.Transparent
//                        )
//                )
//            }
//            items(count = daysInMonths, key = { "box$it" }) { day ->
//                DayBox(
//                    color = if (completedDays.contains(day)) cardAndLog.card.composeColor
//                    else MaterialTheme.colorScheme.surfaceContainerHighest,
//                    number = day + 1
//                )
//            }
//        }

        CalendarGrid(
            daysInMonths = daysInMonths,
            completedDays = completedDays,
            activeColor = cardAndLog.card.composeColor,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        )

    }
}

/**
 * 替代内层 LazyVerticalGrid 的轻量级日历网格组件
 */
@Composable
private fun CalendarGrid(
    daysInMonths: Int,
    completedDays: Set<Int>,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    // 假设前导有 2 个空位置
    val firstDayOffset = 2
    val totalSlots = firstDayOffset + daysInMonths
    val rows = (totalSlots + 6) / 7

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (rowIndex in 0 until rows) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                for (columnIndex in 0 until 7) {
                    val slotIndex = rowIndex * 7 + columnIndex
                    val dayNumber = slotIndex - firstDayOffset + 1

                    if (slotIndex in firstDayOffset until totalSlots) {
                        val isCompleted = completedDays.contains(dayNumber)
                        DayBox(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            color = if (isCompleted) activeColor else MaterialTheme.colorScheme.surfaceContainerHighest,
                            number = dayNumber
                        )
                    } else {
                        // 空白的填充格
                        Spacer(modifier = Modifier.weight(1f))
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