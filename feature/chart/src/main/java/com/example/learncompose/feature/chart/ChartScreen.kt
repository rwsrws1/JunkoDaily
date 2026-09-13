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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(modifier: Modifier = Modifier, uiState: ChartContract.UiState = ChartContract.UiState()) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val cardsAndLogs = uiState.cardsAndLogs
    val initialPage = 11
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { initialPage + 1 }
    )

    val today = remember { LocalDate.now() }
    val currentDay by remember {
        derivedStateOf {
            today.minusMonths((initialPage - pagerState.currentPage).toLong())
        }
    }
    val yearMonthStr by remember {
        derivedStateOf {
            currentDay.format(YEAR_MONTH_FORMATTER)
        }
    }

    Box(modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background))
    {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(yearMonthStr, style = MaterialTheme.typography.titleMedium)
                }
            )
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
                Column(Modifier.fillMaxSize()) {
                    ChartPage(
                        cardsAndLogs = cardsAndLogs,
                        currentYear = currentDay.year,
                        currentMonth = currentDay.monthValue,
                        adaptiveInfo = adaptiveInfo
                    )
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
    adaptiveInfo: WindowAdaptiveInfo
) {

    val scaleFactor =
        if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
            || (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                    && adaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)))
        {
            1.2f
        } else {
            1.0f
        }

    val currentDensity = LocalDensity.current
    // 创建调整过 density 的 LocalDensity 作用域
    val scaledDensity = remember(currentDensity, scaleFactor) {
        Density(
            density = currentDensity.density * scaleFactor,
            fontScale = currentDensity.fontScale * scaleFactor
        )
    }

    val minSize =  if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
        || (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                && adaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)))
    {
        200.dp
    } else {
        150.dp
    }
    val daysInMonths = YearMonth.of(currentYear, currentMonth).lengthOfMonth()

    CompositionLocalProvider(LocalDensity provides scaledDensity) {
        LazyVerticalGrid(
            modifier = modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize),
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
                    completedDays = completedDays,
                )
            }
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
            .aspectRatio(1f / 1.1f)
//            .border(
//                width = 1.dp,
//                color = MaterialTheme.colorScheme.onSurface,
//                shape = MaterialTheme.shapes.medium
//            )
        ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Spacer(Modifier.height(10.dp))
        Text(
            cardAndLog.card.cardText,
            Modifier.align(Alignment.CenterHorizontally),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(10.dp))
        CalendarGrid(
            daysInMonths = daysInMonths,
            completedDays = completedDays,
            activeColor = cardAndLog.card.composeColor,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp)
        )
        Spacer(Modifier.height(5.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            Icon(painterResource(R.drawable.clock_loader_40_24px), null, Modifier.size(15.dp))
            Spacer(Modifier.width(3.dp))
            Text("${completedDays.size * 100 / daysInMonths}%")
            Spacer(Modifier.weight(0.5f))
            VerticalDivider(Modifier.height(10.dp))
            Spacer(Modifier.weight(0.5f))
            Icon(painterResource(R.drawable.check_circle_24px), null, Modifier.size(15.dp))
            Spacer(Modifier.width(3.dp))
            Text("${completedDays.size}")
            Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(5.dp))
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
                            color = if (isCompleted) activeColor else MaterialTheme.colorScheme.surfaceContainerLow,
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