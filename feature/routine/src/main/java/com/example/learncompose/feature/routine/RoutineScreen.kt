package com.example.learncompose.feature.routine

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
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
import androidx.window.core.layout.WindowSizeClass
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(
    modifier: Modifier = Modifier,
    uiState: RoutineContract.UiState = RoutineContract.UiState(),
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val handler = LocalHandler.current
    val scope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
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
            modifier = Modifier.fillMaxSize(),
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
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    }
                ) {
                    Icon(painterResource(R.drawable.add_24px), contentDescription = "")
                }
            }
        ) { paddingValues ->
            Column(
                Modifier.fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
            ) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    scrollState = scrollState,
//                    edgePadding = 4.dp,
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
    }
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

@Preview
@Composable
private fun Preview() {
    AppTheme {
        RoutineScreen()
    }
}