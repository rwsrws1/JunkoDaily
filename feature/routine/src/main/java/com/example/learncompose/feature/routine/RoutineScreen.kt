package com.example.learncompose.feature.routine

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SplitButtonLayout
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.toPath
import androidx.compose.material3.toShape
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learncompose.core.common.rememberSoundManager
import com.example.learncompose.core.designsystem.generateDistinctColorLongs
import com.example.learncompose.core.model.RoutineCardWithLog
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.toSize
import androidx.graphics.shapes.Morph
import androidx.window.core.layout.WindowSizeClass
import com.example.learncompose.core.designsystem.icons.AppIcons
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
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalHandler provides viewModel::handleIntent
    ) {
        RoutineScreen(modifier, uiState)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoutineScreen(
    modifier: Modifier = Modifier,
    uiState: RoutineContract.UiState = RoutineContract.UiState(),
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val handler = LocalHandler.current
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) {
        it != SheetValue.PartiallyExpanded
    }
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

        if (showBottomSheet) {
            var textState by remember { mutableStateOf("") }
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                contentWindowInsets = { WindowInsets() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = "习惯名")
                    Spacer(Modifier.height(10.dp))
                    BasicTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        decorationBox = { innerTextField ->
                            // 使用 Box 配合 Alignment.CenterStart 实现绝对垂直居中
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (textState.isEmpty()) {
                                    // 如果需要占位符（Placeholder），可以在这里写
                                    Text(
                                        text = "请输入习惯名",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField() // 渲染实际的输入文本和光标
                            }
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(text = "颜色")
                    Spacer(Modifier.height(10.dp))
                    LazyRow(
                        state = rowListState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                    ) {
                        items(count = COlOR_LIST.size, key = { it }) { index ->
                            Box(
                                Modifier
                                    .padding(horizontal = 5.dp)
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(COlOR_LIST[index]))
                                    .border(
                                        width = if (selectColor == COlOR_LIST[index]) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        onClick = {
                                            selectColor = COlOR_LIST[index]
                                        }
                                    )
                            ) {
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            handler(
                                RoutineContract.Intent.InsertCard(
                                    cardText = textState,
                                    cardColor = selectColor
                                )
                            )
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }
                    ) {
                        Text("确定")
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {

//                LargeTopAppBar(
//                    title = {
//                        Box(modifier = Modifier.fillMaxWidth()) {
//                            // 1. 折叠状态下显示的内容 (接近完全折叠时显示)
//                            if (collapsedFraction > 0.5f) {
//                                Text(
//                                    text = "折叠状态：精简标题",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier.graphicsLayer {
//                                        // 根据折叠进度控制透明度，实现淡入
//                                        alpha = (collapsedFraction - 0.5f) * 2
//                                    }
//                                )
//                            }
//
//                            // 2. 展开状态下显示的内容 (接近完全展开时显示)
//                            if (collapsedFraction <= 0.5f) {
//
//                                PrimaryScrollableTabRow(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .clipToBounds()
//                                    ,
//                                    selectedTabIndex = pagerState.currentPage,
//                                    scrollState = scrollState,
//                                    indicator = {},
//                                    divider = {},
//                                    minTabWidth = 0.dp
//                                ) {
//                                    tabList.forEachIndexed { index, i ->
//                                        val isSelected = pagerState.currentPage == index
//                                        val tabDate = today.minusDays((tabList.lastIndex - index).toLong())
//
//                                        Tab(
//                                            selected = isSelected,
//                                            onClick = {
//                                                selectPage = index
//                                                scope.launch {
//                                                    pagerState.animateScrollToPage(selectPage)
//                                                }
//                                            },
//                                            modifier = Modifier
//                                                .padding(horizontal = 2.dp)
//                                                .height(50.dp)
//                                                .aspectRatio(1f / 1f)
//                                                .clip(MaterialTheme.shapes.medium)
//                                                .background(if (isSelected) MaterialTheme.colorScheme.onSurface
//                                                else MaterialTheme.colorScheme.surfaceContainer),
//                                            selectedContentColor = MaterialTheme.colorScheme.surfaceContainerLowest,
//                                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
//                                        ) {
//                                            Box(
//                                                contentAlignment = Alignment.Center,
//                                            ) {
//                                                Text(
//                                                    text = "${tabDate.dayOfMonth}",
//                                                    style = MaterialTheme.typography.bodyMedium.copy(
//                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
//                                                    )
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//
//
//                            }
//                        }
//                    },
//                    scrollBehavior = scrollBehavior
//                )

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
                            showBottomSheet = true
                        }
                    ) {
                        Icon(painterResource(R.drawable.add_24px), contentDescription = "")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                Modifier.fillMaxSize()
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
                            val currentHeight = (placeable.height + offset).coerceAtLeast(0f).toInt()

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
                                .background(if (isSelected) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.surfaceContainer),
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


//                        val options = listOf("routine", "chart", "spend")
//                        val unCheckedIcons = listOf(AppIcons.routine, AppIcons.chart, AppIcons.spend)
//                        val checkedIcons = listOf(AppIcons.routineFilled, AppIcons.chartFilled, AppIcons.spendFilled)
//                        val checked = rememberSaveable { mutableStateListOf(false, false, false) }
//                        val interactionSources = remember { List(options.size) { MutableInteractionSource() } }
//                        ButtonGroup(
//                            overflowIndicator = { menuState ->
//                                ButtonGroupDefaults.OverflowIndicator(menuState = menuState)
//                            },
//                            expandedRatio = 1f,
//                        ) {
//                            options.forEachIndexed { index, label ->
//                                customItem(
//                                    buttonGroupContent = {
//                                        val contentPadding = ButtonDefaults.ButtonWithIconContentPadding
//                                        val layoutDirection = LocalLayoutDirection.current
//                                        ToggleButton(
//                                            checked = checked[index],
//                                            onCheckedChange = { checked[index] = it },
//                                            shapes =
//                                                when (index) {
//                                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
//                                                    options.lastIndex ->
//                                                        ButtonGroupDefaults.connectedTrailingButtonShapes()
//                                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
//                                                },
//                                            contentPadding = contentPadding,
//                                            interactionSource = interactionSources[index],
//                                            modifier =
//                                                Modifier.animateWidth(
//                                                    interactionSource = interactionSources[index],
//                                                    compressionLimit =
//                                                        contentPadding.calculateEndPadding(layoutDirection),
//                                                ),
//                                        ) {
//                                            Icon(
//                                                painterResource(if (checked[index]) checkedIcons[index] else unCheckedIcons[index]),
//                                                contentDescription = "Localized description",
//                                            )
//                                            Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
//                                            Text(
//                                                text = label,
//                                                softWrap = false,
//                                                maxLines = 1,
//                                                overflow = TextOverflow.Visible,
//                                            )
//                                        }
//                                    },
//                                    menuContent = {
//                                        DropdownMenuItem(
//                                            leadingIcon = { checkedIcons[index] },
//                                            text = { Text(label) },
//                                            onClick = {},
//                                            interactionSource = interactionSources[index],
//                                        )
//                                    },
//                                )
//                            }
//                        }
//
//
//
//                        var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
//
//                        FlowRow(
//                            Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
//                            verticalArrangement = Arrangement.spacedBy(2.dp),
//                        ) {
//                            options.forEachIndexed { index, label ->
//                                ToggleButton(
//                                    checked = selectedIndex == index,
//                                    onCheckedChange = { selectedIndex = index },
//                                    shapes =
//                                        when (index) {
//                                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
//                                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
//                                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
//                                        },
//                                ) {
//                                    Icon(
//                                        painterResource(if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index]),
//                                        contentDescription = "Localized description",
//                                    )
//                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
//                                    Text(label)
//                                }
//                            }
//                        }
//
//
//
//                        FlowRow(
//                            Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
//                            verticalArrangement = Arrangement.spacedBy(2.dp),
//                        ) {
//                            options.forEachIndexed { index, label ->
//                                ToggleButton(
//                                    checked = checked[index],
//                                    onCheckedChange = { checked[index] = it },
//                                    shapes =
//                                        when (index) {
//                                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
//                                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
//                                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
//                                        },
//                                ) {
//                                    Icon(
//                                        painterResource(if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index]),
//                                        contentDescription = "Localized description",
//                                    )
//                                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
//                                    Text(label)
//                                }
//                            }
//                        }
//
//
//                        Column(verticalArrangement = Arrangement.spacedBy((-6).dp)) {
//                            options.forEachIndexed { index, label ->
//                                val shape =
//                                    when (index) {
//                                        0 ->
//                                            (ButtonGroupDefaults.connectedMiddleButtonShapes().shape
//                                                    as RoundedCornerShape)
//                                                .copy(topStart = CornerSize(100), topEnd = CornerSize(100))
//                                        options.lastIndex ->
//                                            (ButtonGroupDefaults.connectedMiddleButtonShapes().shape
//                                                    as RoundedCornerShape)
//                                                .copy(bottomStart = CornerSize(100), bottomEnd = CornerSize(100))
//                                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes().shape
//                                    }
//                                ToggleButton(
//                                    checked = selectedIndex == index,
//                                    onCheckedChange = { selectedIndex = index },
//                                    shapes =
//                                        ToggleButtonShapes(
//                                            shape = shape,
//                                            pressedShape = ToggleButtonDefaults.pressedShape,
//                                            checkedShape = ButtonGroupDefaults.connectedButtonCheckedShape,
//                                        ),
//                                ) {
//                                    Text(label)
//                                }
//                            }
//                        }

                        LoadingIndicator()


                        TestCard()


//                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                            CardGrid(
//                                cardWithLogs = cardWithLogsForThisPage,
//                                currentDate = pageDate,
//                                onLongClick = { id ->
//                                    deleteCardId = id
//                                    isShowDialog = true
//                                },
//                                adaptiveInfo = adaptiveInfo
//                            )
//                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun ColumnScope.TestCard() {
    val morphProgress = remember { Animatable(0f) }
    val rotationProgress = remember { Animatable(0f) }

    val shape1 = MaterialShapes.Square
    val shape2 = MaterialShapes.Flower
    val morph = remember { Morph(shape1.normalized(), shape2.normalized()) }

    var changeShape by remember { mutableStateOf(false) }
    val path = remember { Path() }
    val scaleMatrix = remember { Matrix() }

    LaunchedEffect(changeShape) {
        val morphAnimationSpec = spring<Float>(dampingRatio = 0.6f, stiffness = 200f)
        launch {
            morphProgress.animateTo(
                targetValue = if (changeShape) 1f else 0f,
                animationSpec = morphAnimationSpec
            )
        }
//        launch {
//            val animationResult = rotationProgress.animateTo(
//                targetValue = if (changeShape) 1f / 4f else 0f,
//                animationSpec = morphAnimationSpec
//            )
//            if (animationResult.endReason == AnimationEndReason.Finished) {
//            }
//        }
    }

    Button(
        onClick = { changeShape = !changeShape },
        modifier = Modifier
            .align(Alignment.End)
            .padding(end = 10.dp)
    ) {
        Text("test")
    }

    val secondColor = MaterialTheme.colorScheme.tertiary
    var targetSize by remember { mutableStateOf(Size.Zero) }
    Box(
        modifier = Modifier
            .size(300.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        // 主卡片容器
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
        ) {
            // 1. 底层：绘制动态 Shape 背景
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .onGloballyPositioned { coordinates ->
                        targetSize = coordinates.size.toSize() // 将尺寸存入状态
                    }
                    .align(Alignment.Center)
                    .drawWithCache {
                        onDrawBehind {
                            val progress = morphProgress.value
                            val rotProgress = rotationProgress.value

                            rotate(rotProgress) {
                                drawPath(
                                    path = processPath(
                                        path = morph.toPath(
                                            progress = progress,
                                            path = path,
                                            startAngle = 0,
                                        ),
                                        size = size,
                                        scaleFactor = 1.0f,
                                        scaleMatrix = scaleMatrix,
                                    ),
                                    color = secondColor,
                                    style = Fill,
                                )
                            }
                        }
                    }
            )

            // 2. 限制层：底部与两侧被动态 Shape 严格裁剪的图片部分
            Image(
                painter = painterResource(R.drawable.brush),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)

                    .graphicsLayer {
                        // 使用图形图层和混合模式，将裁剪范围严格限制在底部及两侧
                        clip = true
                        shape = object : Shape {
                            override fun createOutline(
                                size: Size,
                                layoutDirection: LayoutDirection,
                                density: Density
                            ): Outline {
                                // 创建动态的 Morph Outline
                                val morphedPath = processPath(
                                    path = morph.toPath(
                                        progress = morphProgress.value,
                                        path = path,
                                        startAngle = 0
                                    ),
                                    size = size,
                                    scaleFactor = 1.0f,
                                    scaleMatrix = scaleMatrix
                                )
                                return Outline.Generic(morphedPath)
                            }
                        }
                    }
                    .drawWithContent {
                        // 利用 Canvas 裁切：只绘制顶部 0~100% 以外（即中下区域）的内容，避免与顶层重叠
                        drawContent()
                    }
                    .scale(1.3f)
            )

            // 3. 溢出层：顶部不受 Shape 约束、允许透出的图片部分
            Image(
                painter = painterResource(R.drawable.brush),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)

                    .drawWithContent {
                        // 仅绘制顶部超出/上半部分区域，避免底部溢出
                        clipRect(
                            left = -size.width,
                            top = -size.height, // 允许顶部往上无限延伸绘制
                            right = size.width * 2,
                            bottom = size.height * 0.5f // 裁剪掉下半部分，交给受控的底层绘制
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
                    .scale(1.3f)
            )
        }
    }
}

private fun processPath(
    path: Path,
    size: Size,
    scaleFactor: Float,
    scaleMatrix: Matrix = Matrix(),
): Path {
    scaleMatrix.reset()
    scaleMatrix.apply { scale(x = size.width * scaleFactor, y = size.height * scaleFactor) }
    path.transform(scaleMatrix)
    path.translate(size.center - path.getBounds().center)
    return path
}

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
            100.dp
        } else {
            60.dp
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
                    Spacer(Modifier.weight(1f))
                    Text(
                        item.cardText,
                        Modifier
                            .fillMaxWidth(0.95f)
                            .align(Alignment.CenterHorizontally),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.weight(1f))
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