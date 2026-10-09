package com.junko.junkodaily.feature.chart

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.junko.junkodaily.core.designsystem.Black
import com.junko.junkodaily.core.designsystem.Container
import com.junko.junkodaily.core.designsystem.ContainerLowest
import com.junko.junkodaily.core.designsystem.OnSurface
import com.junko.junkodaily.core.designsystem.White
import com.junko.junkodaily.core.designsystem.icons.AppIcons
import com.junko.junkodaily.core.model.RoutineCard
import com.junko.junkodaily.core.model.RoutineCardsAndLogs
import com.junko.junkodaily.feature.chart.components.MonthChartCard
import com.junko.junkodaily.feature.chart.components.YearChartCard
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

val RoutineCard.composeColor: Color
    get() = Color(this.cardColor)

private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")
private val MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM")
private val YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy")

val FloatAnimatableSaver = Saver<Animatable<Float, AnimationVector1D>, Float>(
    save = { it.value }, // 保存时，只提取当前的 Float 值
    restore = { Animatable(it) } // 恢复时，用保存的 Float 值重新创建 Animatable
)

@Composable
fun ChartViewModelScreen(
    modifier: Modifier = Modifier,
    viewModel: ChartViewModel,
    goBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ChartScreen(modifier, uiState, goBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    modifier: Modifier = Modifier,
    uiState: ChartContract.UiState = ChartContract.UiState(),
    goBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val cardsAndLogs = uiState.cardsAndLogs

    val pageSize = remember { 12 }
    val tabList = List(pageSize) { it }
    var selectPage by remember { mutableIntStateOf(tabList.lastIndex) }
    val pagerState = rememberPagerState(initialPage = selectPage, pageCount = { pageSize })
    val tabScrollState = rememberScrollState()
    var isShowHeader by rememberSaveable { mutableStateOf(false) }

    val today = remember { LocalDate.now() }
    val currentDay by remember {
        derivedStateOf {
            today.minusMonths((tabList.lastIndex - pagerState.currentPage).toLong())
        }
    }
    val yearStr by remember {
        derivedStateOf {
            currentDay.year
        }
    }
    val monthStr by remember {
        derivedStateOf {
            currentDay.monthValue
        }
    }

    var isFabShow by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isFabShow = true
    }

    var isShowWithYear by remember { mutableStateOf(false) }
    var clickCardId by remember { mutableLongStateOf(0) }
    val spatialSpecRect = MaterialTheme.motionScheme.slowSpatialSpec<Rect>()
    val effectSpecFloat = MaterialTheme.motionScheme.slowEffectsSpec<Float>()
    val customBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
        spatialSpecRect
    }

    Box(modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButtonPosition = FabPosition.Center,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        AnimatedVisibility(
                            visible = !isShowWithYear,
                            enter = fadeIn(animationSpec = effectSpecFloat, 0f),
                            exit = fadeOut(animationSpec = effectSpecFloat, 0f)
                        ) {
                        }
                        Card(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(50.dp)
                                .clickable(
                                    onClick = if (!isShowWithYear) {
                                        { isShowHeader = !isShowHeader }
                                    } else {
                                        {}
                                    },
                                    indication = null,
                                    interactionSource = null
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = ContainerLowest
                            )
                        ) {
                            Box(Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Black),
                                contentAlignment = Alignment.Center) {
                                AnimatedContent(
                                    targetState = isShowWithYear || isShowHeader
                                ) { isShow ->
                                    if (isShow) {
                                        Text("$yearStr", style = MaterialTheme.typography.titleMediumEmphasized.copy(color = White))
                                    } else {
                                        Text("$yearStr", style = MaterialTheme.typography.labelSmall.copy(color = White))
                                    }
                                }
                            }
                            AnimatedContent(
                                targetState = isShowWithYear || isShowHeader
                            ) { isShow ->
                                if (!isShow) {
                                    Box(Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.5f / 1f)
                                        .background(ContainerLowest),
                                        contentAlignment = Alignment.Center) {
                                        Text("$monthStr", style = MaterialTheme.typography.titleLarge.copy(color = OnSurface))
                                    }
                                }
                            }
                        }
                    },
                    title = {
                        Text("Junko's Daily")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Container
                    )
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = isFabShow,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ) + fadeIn(animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(), 0.5f),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ) + fadeOut(animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(), 0f)
                ) {
                    FloatingActionButton(
                        modifier = Modifier.padding(bottom = 10.dp),
                        onClick = {
                            if (isShowWithYear) {
                                isShowWithYear = false
                            } else {
                                goBack()
                            }
                        },
                        containerColor = ContainerLowest,
                        contentColor = OnSurface,
                        shape = CircleShape
                    ) {
                        Icon(painterResource(AppIcons.back), null)
                    }
                }
            }
        ) { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Container)
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {

                AnimatedVisibility(
                    visible = isShowHeader
                ) {
                    PrimaryScrollableTabRow(
                        modifier = Modifier.fillMaxWidth(),
                        selectedTabIndex = pagerState.currentPage,
                        scrollState = tabScrollState,
                        indicator = {},
                        divider = {},
                        minTabWidth = 0.dp,
                        containerColor = Container,
                    ) {
                        tabList.forEachIndexed { index, i ->
                            val isSelected = pagerState.currentPage == index
                            val tabDate = today.minusMonths((tabList.lastIndex - index).toLong())

                            Tab(
                                selected = isSelected,
                                onClick = {
                                    selectPage = index
                                    scope.launch {
                                        pagerState.animateScrollToPage(selectPage)
                                    }
                                },
                                modifier = Modifier
                                    .height(60.dp)
                                    .padding(horizontal = 4.dp, vertical = 5.dp)
                                    .aspectRatio(1f / 1f)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(if (isSelected) Black else ContainerLowest),
                                selectedContentColor = Color.White,
                                unselectedContentColor = Container
                            ) {
                                Box(
                                    Modifier.fillMaxHeight(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${tabDate.monthValue}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = isShowWithYear,
                        transitionSpec = {
                            fadeIn(animationSpec = effectSpecFloat, 0f) togetherWith
                            fadeOut(animationSpec = effectSpecFloat, 0f)
                        }
                    ) { isShow ->
                        if (isShow) {
                            YearChartPage(currentYear = currentDay.year,
                                item = cardsAndLogs.first { it.card.id == clickCardId },
                                cardModifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState("card_bounds${clickCardId}"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = customBoundsTransform,
//                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.Crop),
                                    clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.large)),
                                chartModifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState("chart_bounds${clickCardId}"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = customBoundsTransform,
//                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.Crop),
                                    clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.large))
                            )
                            BackHandler {
                                isShowWithYear = false
                            }
                        } else {
                            HorizontalPager(
                                state = pagerState,
                                pageSize = PageSize.Fill,
                                beyondViewportPageCount = 0,
                                pageSpacing = 10.dp,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Container)
                            ) { page ->
                                val currentDay = remember(page, today) {
                                    today.minusMonths((tabList.lastIndex - page).toLong())
                                }

                                val lazyGridState = rememberLazyGridState()
                                LaunchedEffect(lazyGridState) {
                                    var previousIndex = lazyGridState.firstVisibleItemIndex
                                    var previousScrollOffset = lazyGridState.firstVisibleItemScrollOffset
                                    snapshotFlow {
                                        Pair(lazyGridState.firstVisibleItemIndex, lazyGridState.firstVisibleItemScrollOffset)
                                    }.collect { (currentIndex, currentOffset) ->
                                        if (currentIndex > previousIndex) {
                                            isFabShow = false
                                        } else if (currentIndex < previousIndex) {
                                            isFabShow = true
                                        } else {
                                            if (currentOffset > previousScrollOffset + 6) {
                                                isFabShow = false
                                            } else if (currentOffset < previousScrollOffset - 6) {
                                                isFabShow = true
                                            }
                                        }
                                        previousIndex = currentIndex
                                        previousScrollOffset = currentOffset
                                    }
                                }

                                MonthChartPage(
                                    cardsAndLogs = cardsAndLogs,
                                    currentYear = currentDay.year,
                                    currentMonth = currentDay.monthValue,
                                    adaptiveInfo = adaptiveInfo,
                                    state = lazyGridState,
                                    onChartClick = { cardId ->
                                        clickCardId = cardId
                                        isShowHeader = false
                                        isShowWithYear = true
                                    },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = customBoundsTransform,
                                )
                            }
                        }
                    }
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
    adaptiveInfo: WindowAdaptiveInfo,
    state: LazyGridState,
    onChartClick: (Long) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    boundsTransform: BoundsTransform,
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
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize),
            state = state,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = cardsAndLogs, key = { it.card.id }) { item ->
                val completedDays = remember(item.logs, currentYear, currentMonth) {
                    item.logs.asSequence()
                        .filter { it.isCompleted && it.recordDate != null }
                        .filter { it.recordDate!!.year == currentYear && it.recordDate!!.monthValue == currentMonth }
                        .mapTo(HashSet()) { it.recordDate!!.dayOfMonth }
                }
                with(sharedTransitionScope) {
                    MonthChartCard(
                        modifier = modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState("chart_bounds${item.card.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform,
                            clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium)),
                        cardAndLog = item,
                        daysInMonths = daysInMonths,
                        completedDays = completedDays,
                        onChartClick = { onChartClick(item.card.id) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = boundsTransform,
                        cardId = item.card.id
                    )
                }
            }
        }
    }
}

@Composable
private fun YearChartPage(
    cardModifier: Modifier = Modifier,
    chartModifier: Modifier = Modifier,
//    cardsAndLogs: List<RoutineCardsAndLogs>,
    currentYear: Int,
    item: RoutineCardsAndLogs,
) {
//    LazyColumn(
//        modifier = modifier.fillMaxSize(),
//        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
//        verticalArrangement = Arrangement.spacedBy(10.dp),
//        state = rememberLazyListState()
//    ) {
//        items(items = cardsAndLogs, key = { it.card.id }) { item ->

    val validLogs = remember(item.logs, currentYear) {
        item.logs.asSequence()
            .filter { it.isCompleted && it.recordDate != null }
            .filter { it.recordDate!!.year == currentYear }
            .toList()
    }

    val completedMonthDays = List(12) { index ->
        validLogs.count { it.recordDate!!.monthValue == index + 1 }.toFloat()
    }

    YearChartCard(
        cardModifier = cardModifier,
        chartModifier = chartModifier,
        cardAndLog = item,
        completedMonthDays = completedMonthDays,
        currentYear = currentYear,
    )

//        }
//    }
}

@Preview
@Composable
private fun Preview() {
    ChartScreen(uiState = ChartContract.UiState(cardsAndLogs = listOf(RoutineCardsAndLogs())))
}