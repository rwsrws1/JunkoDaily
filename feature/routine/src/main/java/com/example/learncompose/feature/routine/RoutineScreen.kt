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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import com.example.learncompose.core.designsystem.components.TopBarPrimary
import com.example.learncompose.core.designsystem.components.card.LocalCardScopeProvider
import com.example.learncompose.core.designsystem.components.card.ScratchMaskCard
import com.example.learncompose.core.designsystem.generateDistinctColorLongs
import com.example.learncompose.core.model.RoutineCard
import kotlinx.coroutines.launch
import java.time.LocalDate

val RoutineCard.composeColor: Color
    get() = Color(this.color)

val LocalHandler = compositionLocalOf<(RoutineContract.Intent) -> Unit> {
    {}
}

@Composable
fun RoutineViewModelScreen(modifier: Modifier = Modifier, viewModel: RoutineViewModel, onChartClick: () -> Unit = {}) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalHandler provides viewModel::handleIntent
    ) {
        RoutineScreen(modifier, uiState, onChartClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(modifier: Modifier = Modifier, uiState: RoutineContract.UiState = RoutineContract.UiState(), onChartClick: () -> Unit = {}) {
    val cardList = uiState.cardList
    val handler = LocalHandler.current
    val scope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val colorList = remember { generateDistinctColorLongs(30) }
    val rowListState = rememberLazyListState()
    var selectColor: Long by remember { mutableLongStateOf(colorList[0]) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

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
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(text = "颜色")
                    Spacer(Modifier.height(10.dp))
                    LazyRow(
                        state = rowListState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                    ) {
                        items(count= colorList.size, key = { it }) { index ->
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
                                RoutineContract.Intent.InsertRoutineCard(
                                    RoutineCard(text = textState, color = selectColor)
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
                TopBarPrimary(onActionsClick = onChartClick)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                Text("${uiState.currentRecordDate}", Modifier.align(Alignment.CenterHorizontally))
                CompositionLocalProvider(
                    LocalCardScopeProvider provides rememberCoroutineScope()
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CardGrid(cardList)
                    }
                }
            }

        }

    }
}

@Composable
fun CardGrid(gridItemList: List<RoutineCard>) {
    val handler = LocalHandler.current
    val soundManager = rememberSoundManager()
    val background = MaterialTheme.colorScheme.surface
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(60.dp),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = gridItemList, key = { item -> item.id }) { item ->
            ScratchMaskCard(
                frontFaceColor = remember(item.color) {
                    item.composeColor.copy(alpha = 0.1f).compositeOver(background) },
                backFaceColor = item.composeColor,
                onFrontFaceClick = {
                    handler(RoutineContract.Intent.UpdateRoutineCard(item.copy(
                        isCompleted = true,
                        recordDate = LocalDate.now()
                    )))
                    soundManager.playWriteSound()
                },
                onBackFaceClick = {
                    handler(RoutineContract.Intent.UpdateRoutineCard(item.copy(isCompleted = false)))
                    soundManager.playEraserSound()
                }
            ) {
                Column(Modifier.fillMaxSize(0.95f)) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        item.text,
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