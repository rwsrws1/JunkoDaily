package com.example.learncompose.feature.chart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.learncompose.core.designsystem.icons.AppIcons
import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardsAndLogs
import com.example.learncompose.core.model.RoutineDailyLog
import com.example.learncompose.feature.routine.chart.ChartContract
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val RoutineCard.composeColor: Color
    get() = Color(this.cardColor)

private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")
private val YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy")

val FloatAnimatableSaver = Saver<Animatable<Float, AnimationVector1D>, Float>(
    save = { it.value }, // 保存时，只提取当前的 Float 值
    restore = { Animatable(it) } // 恢复时，用保存的 Float 值重新创建 Animatable
)

@Composable
fun ChartViewModelScreen(
    modifier: Modifier = Modifier,
    viewModel: ChartViewModel,
    naviToRoutineScreen: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ChartScreen(modifier, uiState, naviToRoutineScreen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    modifier: Modifier = Modifier,
    uiState: ChartContract.UiState = ChartContract.UiState(),
    naviToRoutineScreen: () -> Unit = {}
) {
    var isShowWithYear by rememberSaveable { mutableStateOf(false) }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val cardsAndLogs = uiState.cardsAndLogs

    val initialPage =
        if (isShowWithYear) {
            9
        } else {
            11
        }
    val pagerState = if (isShowWithYear) {
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { initialPage + 1 }
        )
    } else {
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { initialPage + 1 }
        )
    }

    val today = remember { LocalDate.now() }
    val currentDay by remember(isShowWithYear) {
        if (isShowWithYear) {
            derivedStateOf {
                today.minusYears((initialPage - pagerState.currentPage).toLong())
            }
        } else {
            derivedStateOf {
                today.minusMonths((initialPage - pagerState.currentPage).toLong())

            }
        }
    }
    val yearMonthStr by remember(isShowWithYear) {
        if (isShowWithYear) {
            derivedStateOf {
                currentDay.format(YEAR_FORMATTER)
            }
        } else {
            derivedStateOf {
                currentDay.format(YEAR_MONTH_FORMATTER)
            }
        }
    }

    var fabVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        fabVisible = true
    }

    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )
    {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(yearMonthStr, style = MaterialTheme.typography.titleMedium)
                    }
                )
            },
            floatingActionButton = {
            }
        ) { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                HorizontalPager(
                    state = pagerState,
                    pageSize = PageSize.Fill,
                    beyondViewportPageCount = 0,
                    pageSpacing = 10.dp,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val currentDay = if (isShowWithYear) {
                        remember(page, today) {
                            today.minusYears((initialPage - page).toLong())
                        }
                    } else remember(page, today) {
                        today.minusMonths((initialPage - page).toLong())
                    }
                    if (isShowWithYear) {
                        YearChartPage(
                            cardsAndLogs = cardsAndLogs,
                            currentYear = currentDay.year,
                            currentMonth = currentDay.monthValue,
                            adaptiveInfo = adaptiveInfo
                        )
                    } else {
                        MonthChartPage(
                            cardsAndLogs = cardsAndLogs,
                            currentYear = currentDay.year,
                            currentMonth = currentDay.monthValue,
                            adaptiveInfo = adaptiveInfo
                        )
                    }

                }
            }

        }

        HorizontalFloatingToolbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            expanded = true,
            floatingActionButton = {
                AnimatedVisibility(
                    visible = fabVisible,
                    enter = slideInVertically(
                        // fullHeight 表示从屏幕最底部外侧开始向上滑动
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(1000)
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(1000)
                    ) + fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            isShowWithYear = !isShowWithYear
                        }
                    ) {
                        val selectStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                        val normalStyle = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("月", style = if (isShowWithYear) normalStyle else selectStyle)
                            Text("/")
                            Text("年", style = if (isShowWithYear) selectStyle else normalStyle)
                        }
                    }

                }
            }
        )
        {
            AnimatedVisibility(
                visible = fabVisible,
                enter = slideInVertically(
                    // fullHeight 表示从屏幕最底部外侧开始向上滑动
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(1000)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(1000)
                ) + fadeOut()
            ) {
                IconButton(onClick = {
                    naviToRoutineScreen()
                }) {
                    Icon(painterResource(AppIcons.routine), null)
                }
            }
        }
    }
}

@Composable
private fun MonthChartPage(
    modifier: Modifier = Modifier,
    cardsAndLogs: List<RoutineCardsAndLogs>,
    currentYear: Int,
    currentMonth: Int,
    adaptiveInfo: WindowAdaptiveInfo
) {

    val scaleFactor =
        if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
            || (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                    && adaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND))
        ) {
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

    val minSize =
        if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
            || (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                    && adaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND))
        ) {
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
                MonthChartCard(
                    cardAndLog = item,
                    daysInMonths = daysInMonths,
                    completedDays = completedDays,
                )
            }
        }
    }
}

@Composable
private fun MonthChartCard(
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
        MonthCalendarGrid(
            daysInMonths = daysInMonths,
            completedDays = completedDays,
            activeColor = cardAndLog.card.composeColor,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp)
        )
        Spacer(Modifier.height(5.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            Icon(painterResource(R.drawable.clock_loader_40_24px), null, Modifier.size(15.dp))
            Spacer(Modifier.width(3.dp))
            Text("${completedDays.size * 100 / daysInMonths}%")
            Spacer(Modifier.weight(0.5f))
            VerticalDivider(Modifier.fillMaxHeight(0.6f))
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
private fun MonthCalendarGrid(
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
    Box(
        modifier
            .aspectRatio(1f / 1f)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$number",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    }
}

@Composable
private fun YearChartPage(
    modifier: Modifier = Modifier,
    cardsAndLogs: List<RoutineCardsAndLogs>,
    currentYear: Int,
    currentMonth: Int,
    adaptiveInfo: WindowAdaptiveInfo
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        state = rememberLazyListState()
    ) {

        items(items = cardsAndLogs, key = { it.card.id }) { item ->

            val validLogs = remember(item.logs, currentYear, currentMonth) {
                item.logs.asSequence()
                    .filter { it.isCompleted && it.recordDate != null }
                    .filter { it.recordDate!!.year == currentYear }
                    .toList()
            }

            val completedMonthDays = List(12) { index ->
                validLogs.count { it.recordDate!!.monthValue == index + 1 }.toFloat()
            }

            YearChartCard(
                cardAndLog = item,
                completedMonthDays = completedMonthDays,
            )
        }
    }
}

@Composable
private fun YearChartCard(
    modifier: Modifier = Modifier,
    cardAndLog: RoutineCardsAndLogs,
    completedMonthDays: List<Float>
) {
    val dummyText = "M".repeat(3)
    Card(
        Modifier.aspectRatio(1f / 0.55f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = cardAndLog.card.cardText,
                    Modifier.padding(start = 5.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.weight(1f))
                Icon(painterResource(R.drawable.check_circle_24px), null, Modifier.size(15.dp))
                Spacer(Modifier.width(5.dp))
                Box(modifier = Modifier.width(IntrinsicSize.Min)) {
                    Text(text = dummyText, maxLines = 1, modifier = Modifier.alpha(0f))
                    Text(
                        text = "${completedMonthDays.sum().toInt()}",
                        maxLines = 1,
                        textAlign = TextAlign.End
                    )
                }
                Spacer(Modifier.width(10.dp))
            }
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
            ) {
                AnimatedBarChart(data = completedMonthDays, color = cardAndLog.card.composeColor)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun AnimatedBarChart(
    data: List<Float>,
    color: Color,
) {
    val progress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }
    // 记住 TextMeasurer 用于在 Canvas 中测量和绘制文本
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelSmall

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxValue = 31f
        val barCount = data.size

        // 间距与文本样式定义
        val spacing = 4.dp.toPx()

        // 预留顶部和底部的文本区域高度，防止绘制溢出
        val topPadding = 20.dp.toPx()
        val bottomPadding = 20.dp.toPx()

        // 柱状图实际可用的最大绘制高度
        val chartHeight = size.height - topPadding - bottomPadding

        val totalSpacing = spacing * (barCount - 1)
        val barWidth = (size.width - totalSpacing) / barCount

        data.forEachIndexed { index, value ->
            // 计算当前柱子的动画实际高度
            val targetHeight = (value / maxValue) * chartHeight
            val currentHeight = targetHeight * progress.value

            val xOffset = index * (barWidth + spacing)
            // 柱子底部坐标为：总高度 - 底部预留高度
            val barBottomY = size.height - bottomPadding
            val barTopY = barBottomY - currentHeight

            // 1. 绘制柱状图
            drawRoundRect(
                color = color,
                topLeft = Offset(xOffset, barTopY),
                size = Size(barWidth, currentHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            if (value > 0) {
                // 2. 绘制顶部 Value 文本
                val valueText = value.toInt().toString()
                val valueLayoutResult = textMeasurer.measure(valueText, textStyle)
                // 计算居中 X 轴坐标
                val valueX = xOffset + (barWidth - valueLayoutResult.size.width) / 2
                // 文本跟随柱子顶部移动，并保留少量 Padding
                val valueY = barTopY - valueLayoutResult.size.height - 2.dp.toPx()
                drawText(
                    textMeasurer = textMeasurer,
                    text = valueText,
                    style = textStyle,
                    topLeft = Offset(valueX, valueY)
                )
            }

            // 3. 绘制底部 Index 文本
            val indexText = (index + 1).toString()
            val indexLayoutResult = textMeasurer.measure(indexText, textStyle)
            // 计算居中 X 轴坐标
            val indexX = xOffset + (barWidth - indexLayoutResult.size.width) / 2
            // 放置在柱子底部下方
            val indexY = barBottomY + 4.dp.toPx()
            drawText(
                textMeasurer = textMeasurer,
                text = indexText,
                style = textStyle,
                topLeft = Offset(indexX, indexY)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ChartScreen()
}