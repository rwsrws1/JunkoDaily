package com.example.learncompose.feature.routine

import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.toShape
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learncompose.core.common.rememberSoundManager
import com.example.learncompose.core.designsystem.generateDistinctColorLongs
import com.example.learncompose.core.model.RoutineCardWithLog
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.window.core.layout.WindowSizeClass
import com.example.learncompose.core.designsystem.PresetShapeList
import com.example.learncompose.core.designsystem.icons.AppIcons
import com.example.learncompose.core.designsystem.toComposeColor
import com.example.learncompose.feature.routine.components.CardPickerDialog
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
private val COlOR_LIST: List<Long> by lazy {
    generateDistinctColorLongs(36)
}

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
    var isShowBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val rowListState = rememberLazyListState()
    var selectColor: Long by rememberSaveable { mutableLongStateOf(COlOR_LIST[0]) }
    var isShowDialog by remember { mutableStateOf(false) }
    var deleteCardId by remember { mutableLongStateOf(0) }
    val pageSize = remember { 30 }
    val tabList = List(pageSize) { it }
    var selectPage by remember { mutableIntStateOf(tabList.lastIndex) }
    val pagerState = rememberPagerState(initialPage = selectPage, pageCount = { pageSize })
    val scrollState = rememberScrollState()
    val today = remember { LocalDate.now() }
    val currentDate by remember {
        derivedStateOf {
            today.minusDays((tabList.lastIndex - pagerState.currentPage).toLong())
        }
    }
    val currentLocale = LocalLocale.current.platformLocale
    val monthDayStr = currentDate.format(MONTH_DAY_FORMATTER)
    val dayOfWeekStr = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, currentLocale)
    LaunchedEffect(currentDate) {
        handler(RoutineContract.Intent.SelectDate(currentDate))
    }

//    val scrollBehavior = rememberCollapsedTopAppBarScrollBehavior()
//    // 获取当前的折叠比例 (0.0F完全展开 ~ 1.0F完全折叠)
//    val collapsedFraction = scrollBehavior.state.collapsedFraction

// 1. 获取屏幕密度与 TabRow 高度
    val density = LocalDensity.current
    val headerHeightPx = with(density) { 60.dp.toPx() }
// 2. 声明官方的 TopAppBarState（这相当于你的 headerOffsetPx 状态管理器）
    val topAppBarState = rememberTopAppBarState(
        // 限制最大向上滚动的高度（即 TabRow 的高度）
        initialHeightOffsetLimit = -headerHeightPx,
        // 如果你希望刚进入页面时 TabRow 是隐藏的，就设为 -headerHeightPx；若是展开的则设为 0f
        initialHeightOffset = -headerHeightPx
    )
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

//    val density = LocalDensity.current
//    val headerHeightPx = with(density) { 60.dp.toPx() }
//    var headerOffsetPx by remember { mutableFloatStateOf(-headerHeightPx) }
//    val animatable = remember { Animatable(0f) }
//    val nestedScrollConnection = remember(headerHeightPx) {
//        object : NestedScrollConnection {
//            // 【向上滑动】：优先由 TabRow 拦截并向上收起
//            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                val delta = available.y
//                if (delta < 0) { // 手指向上滑
//                    val newOffset = (headerOffsetPx + delta).coerceIn(-headerHeightPx, 0f)
//                    val consumed = newOffset - headerOffsetPx
//                    headerOffsetPx = newOffset
//                    return Offset(0f, consumed)
//                }
//                return Offset.Zero
//            }
//
//            // 【向下滑动】：当列表滑到顶部且继续下拉时，展开 TabRow
//            override fun onPostScroll(
//                consumed: Offset,
//                available: Offset,
//                source: NestedScrollSource
//            ): Offset {
//                val delta = available.y
//                if (delta > 0) { // 手指向下滑
//                    val newOffset = (headerOffsetPx + delta * 0.7f).coerceIn(-headerHeightPx, 0f)
//                    val consumed = newOffset - headerOffsetPx
//                    headerOffsetPx = newOffset
//                    return Offset(0f, consumed)
//                }
//                return Offset.Zero
//            }
//
//            // 【松手吸附/惯性】：手指抬起触发 Fling 时处理 Header 归位
//            override suspend fun onPreFling(available: Velocity): Velocity {
//                // 只要 Header 处于半开半合状态，就优先处理吸附归位
//                if (headerOffsetPx > -headerHeightPx && headerOffsetPx < 0f) {
//                    val target = when {
//                        available.y < -300f -> -headerHeightPx // 快速向上甩：强制完全收起
//                        available.y > 300f -> 0f               // 快速向下甩：强制完全展开
//                        headerOffsetPx > -headerHeightPx / 2f -> 0f // 慢速松手：根据位置过半展开，否则收起
//                        else -> -headerHeightPx
//                    }
//                    animatable.snapTo(headerOffsetPx)
//                    animatable.animateTo(target) {
//                        headerOffsetPx = value
//                    }
//                    // 消费掉 Velocity，防止网格列表与 Header 吸附动画同时运作产生冲突
//                    return available
//                }
//                return Velocity.Zero
//            }
//
//            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
//                // 保底逻辑：若惯性结束后 Header 依然停留在中间，强制吸附归位
//                if (headerOffsetPx > -headerHeightPx && headerOffsetPx < 0f) {
//                    val target = if (headerOffsetPx > -headerHeightPx / 2f) 0f else -headerHeightPx
//                    animatable.snapTo(headerOffsetPx)
//                    animatable.animateTo(target) {
//                        headerOffsetPx = value
//                    }
//                }
//                return Velocity.Zero
//            }
//        }
//    }

    var fabVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        fabVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {

                TopAppBar(
                    title = {
                        Column() {
                            Text(monthDayStr, style = MaterialTheme.typography.titleMedium)
                            Text(dayOfWeekStr, style = MaterialTheme.typography.titleMedium)
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                repeat(36) { time ->
                                    handler(
                                        RoutineContract.Intent.InsertCard(
                                            cardText = "测试卡片颜色",
                                            cardColor = COlOR_LIST[time]
                                        )
                                    )
                                }
                            }
                        ) {
                            Text("test", color = Color.Transparent)
                        }
                    },
//                    scrollBehavior = scrollBehavior
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

                PrimaryScrollableTabRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clipToBounds() // 1. 裁剪超出布局边界的内容
//                        .layout { measurable, constraints ->
//                            val placeable = measurable.measure(constraints)
//                            // 2. 动态计算 TabRow 在父 Column 中实际占用的测量高度（0 到 placeable.height 之间）
//                            val currentHeight = (placeable.height + headerOffsetPx).coerceAtLeast(0f).toInt()
//
//                            // 3. 报告给 Column 实际占用高度，下方 HorizontalPager 会自动顺滑顶上，无留白
//                            layout(placeable.width, currentHeight) {
//                                placeable.placeRelative(0, headerOffsetPx.toInt())
//                            }
//                        }

                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)

                            // 【直接读取官方引擎计算好的 offset 即可！】
                            val offset = scrollBehavior.state.heightOffset
                            val currentHeight =
                                (placeable.height + offset).coerceAtLeast(0f).toInt()

                            layout(placeable.width, currentHeight) {
                                placeable.placeRelative(0, offset.toInt())
                            }
                        }

                    ,
                    selectedTabIndex = pagerState.currentPage,
                    scrollState = scrollState,
                    indicator = {},
                    divider = {},
                    minTabWidth = 0.dp
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
                                .padding(horizontal = 2.dp)
                                .height(50.dp)
                                .aspectRatio(1f / 1f)
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.surfaceContainer
                                ),
                            selectedContentColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "${tabDate.dayOfMonth}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }

                HorizontalPager(
                    key = { it },
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    pageSize = PageSize.Fill,
                    pageSpacing = 0.dp,
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    beyondViewportPageCount = 0
                ) { page ->
                    val pageDate = remember(page) {
                        today.minusDays((tabList.lastIndex - page).toLong())
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
                                adaptiveInfo = adaptiveInfo
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier
            .align(Alignment.BottomCenter)) {
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
                HorizontalFloatingToolbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    expanded = true,
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                isShowBottomSheet = true
                            }
                        ) {
                            Icon(painterResource(R.drawable.add_24px), contentDescription = "")
                        }
                    }
                ) {
                    IconButton(
                        onClick = {
                            naviToChartScreen()
                        }
                    ) {
                        Icon(painterResource(AppIcons.chart), null)
                    }

                }

            }

        }

        FullscreenCustomOverlay(visible = isShowBottomSheet, onDismiss = { isShowBottomSheet = false })


        if (isShowBottomSheet) {
            var textState by remember { mutableStateOf("") }

//            ModalBottomSheet(
//                onDismissRequest = {
//                    showBottomSheet = false
//                },
//                sheetState = sheetState,
//                contentWindowInsets = { WindowInsets() }
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .imePadding()
//                        .padding(horizontal = 16.dp)
//                ) {
//                    Text(text = "习惯名")
//                    Spacer(Modifier.height(10.dp))
//                    BasicTextField(
//                        value = textState,
//                        onValueChange = { textState = it },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(MaterialTheme.colorScheme.secondaryContainer)
//                            .padding(horizontal = 12.dp),
//                        singleLine = true,
//                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
//                        decorationBox = { innerTextField ->
//                            // 使用 Box 配合 Alignment.CenterStart 实现绝对垂直居中
//                            Box(
//                                modifier = Modifier.fillMaxSize(),
//                                contentAlignment = Alignment.CenterStart
//                            ) {
//                                if (textState.isEmpty()) {
//                                    // 如果需要占位符（Placeholder），可以在这里写
//                                    Text(
//                                        text = "请输入习惯名",
//                                        color = Color.Gray,
//                                        fontSize = 14.sp
//                                    )
//                                }
//                                innerTextField() // 渲染实际的输入文本和光标
//                            }
//                        },
//                    )
//                    Spacer(Modifier.height(10.dp))
//                    Text(text = "颜色")
//                    Spacer(Modifier.height(10.dp))
//                    LazyRow(
//                        state = rowListState,
//                        modifier = Modifier.fillMaxWidth(),
//                        contentPadding = PaddingValues(horizontal = 10.dp),
//                    ) {
//                        items(count = COlOR_LIST.size, key = { it }) { index ->
//                            Box(
//                                Modifier
//                                    .padding(horizontal = 5.dp)
//                                    .size(50.dp)
//                                    .clip(CircleShape)
//                                    .background(Color(COlOR_LIST[index]))
//                                    .border(
//                                        width = if (selectColor == COlOR_LIST[index]) 2.dp else 0.dp,
//                                        color = MaterialTheme.colorScheme.onSurface,
//                                        shape = CircleShape
//                                    )
//                                    .clickable(
//                                        onClick = {
//                                            selectColor = COlOR_LIST[index]
//                                        }
//                                    )
//                            ) {
//                            }
//                        }
//                    }
//                    Spacer(Modifier.height(20.dp))
//                    Button(
//                        modifier = Modifier.fillMaxWidth(),
//                        onClick = {
//                            handler(
//                                RoutineContract.Intent.InsertCard(
//                                    cardText = textState,
//                                    cardColor = selectColor
//                                )
//                            )
//                            scope.launch { sheetState.hide() }.invokeOnCompletion {
//                                if (!sheetState.isVisible) {
//                                    showBottomSheet = false
//                                }
//                            }
//                        }
//                    ) {
//                        Text("确定")
//                    }
//                    Spacer(Modifier.height(20.dp))
//                }
//            }
        }

    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CardGrid(
    cardWithLogs: List<RoutineCardWithLog>,
    currentDate: LocalDate,
    onLongClick: (Long) -> Unit,
    adaptiveInfo: WindowAdaptiveInfo
) {
    val minSize =
        if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
            || (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                    && adaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND))
        ) {
            120.dp
        } else {
            90.dp
        }
    val handler = LocalHandler.current
    val soundManager = rememberSoundManager()
    val background = MaterialTheme.colorScheme.background
    val state = rememberLazyGridState()
    val currentInstant by rememberUpdatedState(Instant.now())
    val currentDate by rememberUpdatedState(currentDate)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize),
        state = state,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = cardWithLogs, key = { item -> item.cardId }) { item ->
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
            ScratchMaskCard(
                cardId = item.cardId,
                frontFaceColor = item.composeColor.copy(alpha = 0.05f).compositeOver(background),
                backFaceColor = item.composeColor,
                onFrontFaceClick = onFrontFaceClick,
                onBackFaceClick = onBackFaceClick,
                isFrontFace = !item.isCompleted,
                onLongClick = onLongClick
            ) {
                Column(Modifier.fillMaxSize(0.95f)) {
                    Spacer(Modifier.weight(0.1f))
                    Box(Modifier.fillMaxWidth().weight(0.5f)) {
                        Box(Modifier.fillMaxHeight().aspectRatio(1f).align(Alignment.Center)
                            .background(color = item.cardColor.toComposeColor().copy(0.3f)
                                .compositeOver(MaterialTheme.colorScheme.surfaceContainerLowest),
                                shape = PresetShapeList[item.cardShapeIndex].toShape())) {}
                        Text(
                            item.cardText,
                            Modifier
                                .fillMaxWidth(0.95f)
                                .align(Alignment.Center),
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.weight(0.1f))
                    if(item.cardImage != 0) {
                        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {

                            Image(
                                painterResource(item.cardImage), null,
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    Spacer(Modifier.weight(0.1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberCollapsedTopAppBarScrollBehavior(
    initialState: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(),
    flungAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
): TopAppBarScrollBehavior {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = initialState,
        canScroll = canScroll,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flungAnimationSpec
    )

    LaunchedEffect(scrollBehavior) {
        // 自动在测量完成后重置为折叠状态
        scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffsetLimit
    }

    return scrollBehavior
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        RoutineScreen()
    }
}