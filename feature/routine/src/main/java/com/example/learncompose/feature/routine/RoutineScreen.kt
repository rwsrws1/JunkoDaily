package com.example.learncompose.feature.routine

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learncompose.core.designsystem.theme.AppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learncompose.core.common.rememberSoundManager
import com.example.learncompose.core.model.RoutineCardWithLog
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.window.core.layout.WindowSizeClass
import com.example.learncompose.core.designsystem.Container
import com.example.learncompose.core.designsystem.Black
import com.example.learncompose.core.designsystem.ContainerLowest
import com.example.learncompose.core.designsystem.OnSurface
import com.example.learncompose.core.designsystem.PresetColorList
import com.example.learncompose.core.designsystem.White
import com.example.learncompose.core.designsystem.components.ImageAreaCard
import com.example.learncompose.core.designsystem.property.PresetImage
import com.example.learncompose.core.designsystem.property.PresetShape
import com.example.learncompose.core.designsystem.toCompositeOverSurface
import com.example.learncompose.feature.routine.components.AnimatedGridItem
import com.example.learncompose.feature.routine.components.ExplosionConfetti
import com.example.learncompose.feature.routine.components.FullscreenCustomOverlay
import com.example.learncompose.feature.routine.components.ScratchMaskCard
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

val RoutineCardWithLog.composeColor: Color
    get() = Color(this.cardColor)

val LocalHandler = compositionLocalOf<(RoutineContract.Intent) -> Unit> {
    {}
}

private val MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd")

@Composable
fun RoutineViewModelScreen(
    modifier: Modifier = Modifier,
    viewModel: RoutineViewModel,
    naviToChartScreen: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalHandler provides viewModel::handleIntent
    ) {
        RoutineScreen(modifier, uiState, naviToChartScreen)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoutineScreen(
    modifier: Modifier = Modifier,
    uiState: RoutineContract.UiState = RoutineContract.UiState(),
    naviToChartScreen: () -> Unit = {}
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val handler = LocalHandler.current
    val scope = rememberCoroutineScope()
    var isShowCardPicker by remember { mutableStateOf(false) }
    var isShowDialog by remember { mutableStateOf(false) }
    var deleteCardId by remember { mutableLongStateOf(0) }

    val pageSize = remember { 30 }
    val tabList = List(pageSize) { it }
    var selectPage by remember { mutableIntStateOf(tabList.lastIndex) }
    val pagerState = rememberPagerState(initialPage = selectPage, pageCount = { pageSize })
    val tabScrollState = rememberScrollState()
    var isShowHeader by rememberSaveable { mutableStateOf(false) }

    val today = remember { LocalDate.now() }
    val currentDate by remember {
        derivedStateOf {
            today.minusDays((tabList.lastIndex - pagerState.currentPage).toLong())
        }
    }
    val currentLocale = LocalLocale.current.platformLocale
    val monthDayStr by remember {
        derivedStateOf {
            currentDate.dayOfMonth
        }
    }
    val monthStr by remember {
        derivedStateOf {
            currentDate.month.getDisplayName(TextStyle.SHORT, currentLocale)
        }
    }
    val dayOfWeekStr = currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, currentLocale)
    LaunchedEffect(currentDate) {
        handler(RoutineContract.Intent.SelectDate(currentDate))
    }

//    val density = LocalDensity.current
//    val headerHeightPx = with(density) { 60.dp.toPx() }
//    val topAppBarState = rememberTopAppBarState(
//        initialHeightOffsetLimit = -headerHeightPx,
//        initialHeightOffset = -headerHeightPx
//    )
//    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    var isFabShow by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isFabShow = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        if (isShowDialog) {
            AlertDialog(
                onDismissRequest = {
                    isShowDialog = false
                },
                confirmButton = {
                    Button(
                        onClick = {
                            handler(RoutineContract.Intent.DeleteCardById(deleteCardId))
                            isShowDialog = false
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            isShowDialog = false
                        }
                    ) {
                        Text("取消")
                    }
                },
                icon = {
                    Icon(painter = painterResource(R.drawable.mop_24px), null)
                },
                title = {
                    Text("删除卡牌")
                },
                text = {
                    Text("这将删除这个习惯的所有记录，确定吗？")
                }
            )
        }



        Scaffold(
            floatingActionButtonPosition = FabPosition.Center,
            modifier = Modifier
                .fillMaxSize()
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
                    ,
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        Card(
                            modifier = Modifier.padding(start = 10.dp).size(50.dp).clickable(
                                onClick = {
//                                    val isOpen = scrollBehavior.state.heightOffset == 0f
//                                    val targetOffset = if (isOpen) -headerHeightPx else 0f
//                                    val initialValue = if (isOpen) 0f else -headerHeightPx
//                                    val animatable = Animatable(initialValue)
//                                    scope.launch {
//                                        animatable.animateTo(targetOffset) {
//                                            scrollBehavior.state.heightOffset = value
//                                        }
//                                    }
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
                                AnimatedContent(
                                    targetState = isShowHeader
                                ) { isShow ->
                                    if (!isShow) {
                                        Text(text = dayOfWeekStr, style = MaterialTheme.typography.labelSmall.copy(color = White))
                                    } else {
                                        Text(text = monthStr, style = MaterialTheme.typography.titleMediumEmphasized.copy(color = White))
                                    }
                                }
                            }
                            AnimatedContent(
                                targetState = isShowHeader
                            ) { isShow ->
                                if (!isShow) {
                                    Box(Modifier.fillMaxWidth().aspectRatio(1.5f/1f).background(ContainerLowest),
                                        contentAlignment = Alignment.Center) {
                                        Text("$monthDayStr", style = MaterialTheme.typography.titleLarge.copy(color = OnSurface))
                                    }
                                }
                            }
                        }
                    },
                    title = {
                        Text("Junko's Daily")
                    },
                    actions = {
//                        TextButton(
//                            onClick = {
//                                repeat(PresetColorList.size) { time ->
//                                    handler(
//                                        RoutineContract.Intent.InsertCard(
//                                            cardText = "测试卡片",
//                                            cardColor = PresetColorList[time],
//                                            cardShape = "Circle",
//                                            cardImage = PresetImage.defaultImage.resName
//                                        )
//                                    )
//                                }
//                            },
//                        ) {
//                            Text("test", color = Color.Transparent)
//                        }
                        IconButton(
                            onClick = {
                                naviToChartScreen()
                            }
                        ) {
                            Icon(painterResource(R.drawable.bar_chart_24px), null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Container
                    )
//                    scrollBehavior = scrollBehavior
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
                            isShowCardPicker = true
                        },
                        containerColor = ContainerLowest,
                        contentColor = OnSurface
                    ) {
                        Icon(painterResource(R.drawable.add_24px), contentDescription = ""
                        , tint = OnSurface)
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
                        modifier = Modifier
                            .fillMaxWidth()
//                        .layout { measurable, constraints ->
//                            val placeable = measurable.measure(constraints)
//
//                            // 【直接读取官方引擎计算好的 offset 即可！】
//                            val offset = scrollBehavior.state.heightOffset
//                            val currentHeight =
//                                (placeable.height + offset).coerceAtLeast(0f).toInt()
//
//                            layout(placeable.width, currentHeight) {
//                                placeable.placeRelative(0, offset.toInt())
//                            }
//                        }
                        ,
                        selectedTabIndex = pagerState.currentPage,
                        scrollState = tabScrollState,
                        indicator = {},
                        divider = {},
                        minTabWidth = 0.dp,
                        containerColor = Container,
                    ) {
                        tabList.forEachIndexed { index, i ->
                            val isSelected = pagerState.currentPage == index
                            val tabDate = today.minusDays((tabList.lastIndex - index).toLong())

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
                                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "${tabDate.dayOfMonth}",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                        Text(text = dayOfWeekStr, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalPager(
                    key = { it },
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Container),
                    pageSize = PageSize.Fill,
                    pageSpacing = 0.dp,
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    beyondViewportPageCount = 0
                ) { page ->
                    val pageDate = remember(page) {
                        today.minusDays((tabList.lastIndex - page).toLong())
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

                    val cardWithLogsForThisPage = uiState.cardWithLogsMap[pageDate] ?: emptyList()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CardGrid(
                                cardWithLogs = cardWithLogsForThisPage,
                                currentDate = pageDate,
                                onLongClick = { id ->
                                    deleteCardId = id
                                    isShowDialog = true
                                },
                                adaptiveInfo = adaptiveInfo,
                                state = lazyGridState
                            )
                        }
                    }
                }
            }
        }


        FullscreenCustomOverlay(isShowCardPicker = isShowCardPicker, onDismiss = { isShowCardPicker = false })

    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CardGrid(
    cardWithLogs: List<RoutineCardWithLog>,
    currentDate: LocalDate,
    onLongClick: (Long) -> Unit,
    adaptiveInfo: WindowAdaptiveInfo,
    state: LazyGridState
) {
    val count =
        if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
            || (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                    && adaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND))
        ) {
            4
        } else {
            3
        }
    val handler = LocalHandler.current
    val soundManager = rememberSoundManager()
    val currentInstant by rememberUpdatedState(Instant.now())
    val currentDate by rememberUpdatedState(currentDate)
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(count),
        state = state,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(items = cardWithLogs, key = { index, item -> item.cardId }) { index, item ->
            var isShowConfetti by remember(item.cardId) { mutableStateOf(false) }
            val onFrontFaceClick = remember(item.cardId) {
                {
                    soundManager.playWriteSound()
                    handler(
                        RoutineContract.Intent.UpsertDailyLog(
                            cardId = item.cardId,
                            recordDate = currentDate,
                            isCompleted = true,
                            completedAt = currentInstant
                        )
                    )
                }
            }
            val onBackFaceClick = remember(item.cardId) {
                {
                    soundManager.playEraserSound()
                    handler(
                        RoutineContract.Intent.UpsertDailyLog(
                            cardId = item.cardId,
                            recordDate = currentDate,
                            isCompleted = false,
                            completedAt = currentInstant
                        )
                    )
                }
            }
            val onLongClick = remember(item.cardId) {
                { onLongClick(item.cardId) }
            }
            val shape = remember(item.cardId) {
                PresetShape.fromName(item.cardShape).polygon
            }
            val animColor by animateColorAsState(
                targetValue = if (item.isCompleted) item.composeColor else item.composeColor.toCompositeOverSurface(),
                animationSpec = tween(2000),
                finishedListener = {
                    if (item.isCompleted) {
                        isShowConfetti = true
                        soundManager.playCheerSound()
                    }
                }
            )
            ScratchMaskCard(
                frontFaceColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                backFaceColor = item.composeColor.toCompositeOverSurface(),
                onFrontFaceClick = onFrontFaceClick,
                onBackFaceClick = onBackFaceClick,
                isFrontFace = !item.isCompleted,
                onLongClick = onLongClick
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.weight(2f))
                    Box(Modifier.fillMaxWidth(0.9f), contentAlignment = Alignment.Center) {
                        ImageAreaCard(
                            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f),
                            targetShape = shape,
                            imageColor =  animColor,
                            selectImage = item.cardImage,
                            onImageClick = null,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = item.cardText,
                        modifier = Modifier.fillMaxWidth(0.9f),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                    Spacer(Modifier.weight(1f))
                }
            }
            if (isShowConfetti) {
                ExplosionConfetti(
                    modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f),
                    maxRadius = 500f, // Controls explosion size limit
                    primaryColors = listOf(
                        Color(0xFFFFD700), // Gold
                        Color(0xFFFF4081), // Pink
                        Color(0xFF00E676)  // Bright Green
                    ),
                    particleCount = 100,
                    durationMillis = 1500,
                    onAnimationEnd = { isShowConfetti = false }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        RoutineScreen(uiState = RoutineContract.UiState(cardWithLogsMap = mapOf(LocalDate.now() to
            listOf(RoutineCardWithLog()))))
    }
}