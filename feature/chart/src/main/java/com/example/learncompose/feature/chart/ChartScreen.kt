package com.example.learncompose.feature.chart

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.material3.toShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import com.example.learncompose.core.designsystem.Black
import com.example.learncompose.core.designsystem.Container
import com.example.learncompose.core.designsystem.ContainerLowest
import com.example.learncompose.core.designsystem.OnSurface
import com.example.learncompose.core.designsystem.White
import com.example.learncompose.core.designsystem.components.ImageAreaCard
import com.example.learncompose.core.designsystem.icons.AppIcons
import com.example.learncompose.core.designsystem.property.PresetImage
import com.example.learncompose.core.designsystem.property.PresetShape
import com.example.learncompose.core.designsystem.toComposeColor
import com.example.learncompose.core.designsystem.toCompositeOverSurface
import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardsAndLogs
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val RoutineCard.composeColor: Color
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
            currentDay.format(YEAR_FORMATTER)
        }
    }
    val monthStr by remember {
        derivedStateOf {
            currentDay.format(MONTH_FORMATTER)
        }
    }

    val lazyGridState = rememberLazyGridState()
    var isFabShow by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isFabShow = true
    }
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

    var isShowWithYear by remember { mutableStateOf(false) }
    var clickCardId by remember { mutableLongStateOf(0) }
    var isShowNavigationIcon by remember { mutableStateOf(true) }
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
                            visible = isShowNavigationIcon,
                            enter = fadeIn(animationSpec = effectSpecFloat, 0f),
                            exit = fadeOut(animationSpec = effectSpecFloat, 0f)
                        ) {
                            Card(
                                modifier = Modifier.padding(start = 10.dp).size(50.dp).clickable(
                                    onClick = {
                                        isShowHeader = !isShowHeader
                                    },
                                    indication = null,
                                    interactionSource = null
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = ContainerLowest
                                )
                            ) {
                                Box(Modifier.fillMaxWidth().weight(1f).background(Black)
                                    , contentAlignment = Alignment.Center) {
                                    Text(yearStr, style = MaterialTheme.typography.labelSmall.copy(color = White))
                                }
                                Box(Modifier.fillMaxWidth().weight(2.7f).background(ContainerLowest),
                                    contentAlignment = Alignment.Center) {
                                    Text(monthStr, style = MaterialTheme.typography.titleLarge.copy(color = OnSurface))
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
                            naviToRoutineScreen()
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
                                        style = MaterialTheme.typography.bodyMedium.copy(
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
                            YearChartPage(currentYear = today.year,
                                item = cardsAndLogs.first { it.card.id == clickCardId },
                                modifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState("detail_element${clickCardId}"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = customBoundsTransform,
//                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.Crop),
                                    clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.large)))
                            BackHandler {
                                isShowWithYear = false
                                isFabShow = true
                                isShowNavigationIcon = true
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
                                MonthChartPage(
                                    cardsAndLogs = cardsAndLogs,
                                    currentYear = currentDay.year,
                                    currentMonth = currentDay.monthValue,
                                    adaptiveInfo = adaptiveInfo,
                                    state = lazyGridState,
                                    onChartClick = { cardId ->
                                        clickCardId = cardId
                                        isShowNavigationIcon = false
                                        isFabShow = false
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
                            sharedContentState = rememberSharedContentState("detail_element${item.card.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform,
                            clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium)),
                        cardAndLog = item,
                        daysInMonths = daysInMonths,
                        completedDays = completedDays,
                        onChartClick = { onChartClick(item.card.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MonthChartCard(
    modifier: Modifier = Modifier,
    cardAndLog: RoutineCardsAndLogs,
    daysInMonths: Int,
    completedDays: Set<Int>,
    onChartClick: () -> Unit
) {
    val boxShape = PresetShape.fromName(cardAndLog.card.cardShape).polygon.toShape()
    val imageId = PresetImage.fromResName(cardAndLog.card.cardImage).resId
    val cardColor = cardAndLog.card.cardColor.toComposeColor()
    val cardText = cardAndLog.card.cardText
    Card(
        onClick = {
            onChartClick()
        },
        modifier = modifier.aspectRatio(1f / 1.1f),
        colors = CardDefaults.cardColors(
            containerColor = ContainerLowest
        ),
    ) {
        Spacer(Modifier.height(10.dp))
        Text(
            cardText,
            Modifier.align(Alignment.CenterHorizontally),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(10.dp))
        Box(Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 10.dp)) {
            MonthCalendarGrid(
                daysInMonths = daysInMonths,
                completedDays = completedDays,
                activeColor = cardColor,
                modifier = Modifier.fillMaxSize(),
                boxShape = boxShape
            )
            Image(
                painterResource(imageId), null,
                Modifier.align(Alignment.Center),
                alpha = 0.2f
            )
        }

        Spacer(Modifier.height(5.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))

            Box(Modifier.size(15.dp).clip(boxShape).background(cardColor))
            Spacer(Modifier.weight(0.05f))
            Box {
                Text("00", Modifier.alpha(0f))
                Text("${completedDays.size}")
            }
            Spacer(Modifier.weight(0.1f))

            Box(Modifier.size(15.dp).clip(boxShape).background(MaterialTheme.colorScheme.surfaceContainerHighest))
            Spacer(Modifier.weight(0.05f))
            Box {
                Text("00", Modifier.alpha(0f))
                Text("${daysInMonths - completedDays.size}")
            }
            Spacer(Modifier.weight(0.1f))

//            VerticalDivider(Modifier.fillMaxHeight(0.6f))
//            Spacer(Modifier.weight(0.1f))
//            Text("${completedDays.size * 100 / daysInMonths}%")
//            Spacer(Modifier.weight(0.1f))
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
    modifier: Modifier = Modifier,
    boxShape: Shape
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
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                for (columnIndex in 0 until 7) {
                    val slotIndex = rowIndex * 7 + columnIndex
                    val dayNumber = slotIndex - firstDayOffset + 1

                    if (slotIndex in firstDayOffset until totalSlots) {
                        val isCompleted = completedDays.contains(dayNumber)
                        DayBox(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .aspectRatio(1f / 1f)
//                                .clip(MaterialTheme.shapes.extraSmall)
                                .clip(boxShape)
                                .background(if (isCompleted) activeColor
                                else MaterialTheme.colorScheme.surfaceContainerHighest),
                            number = dayNumber,
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
fun DayBox(modifier: Modifier = Modifier, number: Int) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$number",
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = ContainerLowest,
            letterSpacing = 0.sp,
        )
    }
}

@Composable
private fun YearChartPage(
    modifier: Modifier = Modifier,
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
        modifier = modifier,
        cardAndLog = item,
        completedMonthDays = completedMonthDays,
        currentYear = currentYear,
    )



//        }
//    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun YearChartCard(
    modifier: Modifier = Modifier,
    cardAndLog: RoutineCardsAndLogs,
    completedMonthDays: List<Float>,
    currentYear: Int,
) {
    val cardColor = cardAndLog.card.cardColor.toComposeColor()
    val cardShape = PresetShape.fromName(cardAndLog.card.cardShape).polygon
    val cardText = cardAndLog.card.cardText
    val cardImage = cardAndLog.card.cardImage
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(0.1f))
        Card(Modifier.fillMaxWidth(0.4f).aspectRatio(1f / 1.5f).align(Alignment.CenterHorizontally),
            colors = CardDefaults.cardColors(
                containerColor = cardColor.toCompositeOverSurface()),
            shape = MaterialTheme.shapes.large
        ) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.weight(1f))
                Box(Modifier.fillMaxWidth(0.9f), contentAlignment = Alignment.Center) {
                    ImageAreaCard(
                        modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f),
                        targetShape = cardShape,
                        imageColor =  cardColor,
                        selectImage = cardImage,
                        onImageClick = null,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = cardText,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                )
                Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.weight(0.1f))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Card(
                modifier = modifier.fillMaxWidth(0.9f).aspectRatio(1f / 0.6f),
                colors = CardDefaults.cardColors(
                    containerColor = ContainerLowest
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    Modifier.fillMaxSize()
                ) {
                    Spacer(Modifier.weight(0.3f))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.weight(0.3f))
                        Text(
                            text = "$currentYear",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(4f))
                        Box(Modifier.size(15.dp).clip(cardShape.toShape())
                            .background(cardColor)
                        )
                        Spacer(Modifier.weight(0.1f))
                        Box {
                            Text(text = "mmm", modifier = Modifier.alpha(0f))
                            Text(
                                text = "${completedMonthDays.sum().toInt()}",
                                maxLines = 1,
                                textAlign = TextAlign.End
                            )
                        }
                        Spacer(Modifier.weight(0.1f))
                    }
                    Spacer(Modifier.weight(0.1f))
                    Box(
                        Modifier.weight(4f).padding(horizontal = 10.dp),
                    ) {
                        AnimatedBarChart(data = completedMonthDays, color = cardAndLog.card.composeColor)
                    }
                    Spacer(Modifier.weight(0.1f))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }

}

@Composable
fun AnimatedBarChart(
    data: List<Float>,
    color: Color,
) {
    // 记住 TextMeasurer 用于在 Canvas 中测量和绘制文本
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelSmall.copy(color = OnSurface)

    val progress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }
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
    ChartScreen(uiState = ChartContract.UiState(cardsAndLogs = listOf(RoutineCardsAndLogs())))
}