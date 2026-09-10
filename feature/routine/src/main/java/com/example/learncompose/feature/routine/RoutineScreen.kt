package com.example.learncompose.feature.routine

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learncompose.core.common.rememberSoundManager
import com.example.learncompose.core.designsystem.components.TopBarPrimary
import com.example.learncompose.core.designsystem.components.card.LocalCardScopeProvider
import com.example.learncompose.core.designsystem.components.card.ScratchMaskCard
import com.example.learncompose.core.designsystem.generateDistinctColorLongs
import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardWithLog
import com.example.learncompose.core.model.RoutineDailyLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.platform.LocalLocale

val RoutineCardWithLog.composeColor: Color
    get() = Color(this.cardColor)

val LocalHandler = compositionLocalOf<(RoutineContract.Intent) -> Unit> {
    {}
}

@Composable
fun RoutineViewModelScreen(
    modifier: Modifier = Modifier,
    viewModel: RoutineViewModel,
    onChartClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalHandler provides viewModel::handleIntent
    ) {
        RoutineScreen(modifier, uiState, onChartClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(
    modifier: Modifier = Modifier,
    uiState: List<RoutineContract.UiState> = listOf(RoutineContract.UiState()),
    onChartClick: () -> Unit = {}
) {
    val handler = LocalHandler.current
    val scope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val colorList = remember { generateDistinctColorLongs(30) }
    val rowListState = rememberLazyListState()
    var selectColor: Long by remember { mutableLongStateOf(colorList[0]) }
    val focusRequester = remember { FocusRequester() }
    var isShowDialog by remember { mutableStateOf(false) }
    var deleteCardId by remember { mutableLongStateOf(0) }
    val initialPage = 29
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { 30 }
    )

//    var isShowDatePicker by remember { mutableStateOf(false) }
//    val todayUtcMillis = LocalDate.now()
//        .atStartOfDay(ZoneId.of("UTC"))
//        .toInstant()
//        .toEpochMilli()
//    val datePickerState = rememberDatePickerState(
//        selectableDates = object : SelectableDates {
//            // 限制日历上的具体某一天是否可选
//            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
//                return utcTimeMillis >= todayUtcMillis
//            }
//
//            // （可选）限制年份下拉菜单中的可选项
//            override fun isSelectableYear(year: Int): Boolean {
//                return year >= LocalDate.now().year
//            }
//        }
//    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

//        if (isShowDatePicker) {
//            DatePickerDialog(
//                onDismissRequest = { isShowDatePicker = false },
//                confirmButton = {
//                    TextButton(onClick = {
//                        isShowDatePicker = false
//                    }) {
//                        Text("OK")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { isShowDatePicker = false }) {
//                        Text("Cancel")
//                    }
//                }
//            ) {
//                DatePicker(state = datePickerState)
//            }
//        }

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
                sheetState = sheetState
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
                            .padding(horizontal = 12.dp)
                            .focusRequester(focusRequester),
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
                    Spacer(Modifier.height(20.dp))
                    Text(text = "颜色")
                    Spacer(Modifier.height(10.dp))
                    LazyRow(
                        state = rowListState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                    ) {
                        items(count = colorList.size, key = { it }) { index ->
                            Box(
                                Modifier
                                    .padding(horizontal = 5.dp)
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorList[index]))
                                    .border(
                                        width = if (selectColor == colorList[index]) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        onClick = {
                                            selectColor = colorList[index]
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
                                    RoutineCard(cardText = textState, cardColor = selectColor)
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


                    SideEffect {
//                        focusRequester.requestFocus()
                    }
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBarPrimary()
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
            CompositionLocalProvider(
                LocalCardScopeProvider provides rememberCoroutineScope()
            ) {
                HorizontalPager(
                    key = { it },
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    pageSize = PageSize.Fill,
                    pageSpacing = 0.dp,
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    beyondViewportPageCount = 0
                ) { page ->

                    val today = LocalDate.now()
                    val currentData = today.minusDays((initialPage - page).toLong())

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues)
                    ) {
                        Text("$currentData  ${currentData.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, LocalLocale.current.platformLocale)}", Modifier.align(Alignment.CenterHorizontally))
                        Spacer(Modifier.height(10.dp))
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CardGrid(uiState[initialPage - page], currentData, onLongClick = { id ->
                                deleteCardId = id
                                isShowDialog = true
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardGrid(
    uiState: RoutineContract.UiState,
    currentData: LocalDate,
    onLongClick: (Long) -> Unit = {}
) {
    val cardList = uiState.routineCardWithLogList
    val handler = LocalHandler.current
    val soundManager = rememberSoundManager()
    val background = MaterialTheme.colorScheme.background
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(60.dp),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = cardList, key = { item -> item.cardId }) { item ->
            ScratchMaskCard(
                frontFaceColor = remember(item.cardColor) {
                    item.composeColor.copy(alpha = 0.05f).compositeOver(background)
                },
                backFaceColor = item.composeColor,
                onFrontFaceClick = {
                    handler(
                        RoutineContract.Intent.UpsertDailyLog(
                            RoutineDailyLog(
                                cardId = item.cardId,
                                recordDate = currentData,
                                isCompleted = true,
                                completedAt = item.completedAt
                            )
                        )
                    )
                    soundManager.playWriteSound()
                },
                onBackFaceClick = {
                    handler(
                        RoutineContract.Intent.UpsertDailyLog(
                            RoutineDailyLog(
                                cardId = item.cardId,
                                recordDate = currentData,
                                isCompleted = false,
                                completedAt = item.completedAt
                            )
                        )
                    )
                    soundManager.playEraserSound()
                },
                isFrontColor = !item.isCompleted,
                onLongClick = {
                    onLongClick(item.cardId)
                }
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