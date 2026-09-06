package com.example.learncompose.feature.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import com.example.learncompose.core.designsystem.components.SearchComponent
import com.example.learncompose.core.designsystem.components.card.LocalCardScopeProvider
import com.example.learncompose.core.designsystem.components.card.ScratchMaskCard
import com.example.learncompose.core.designsystem.generateDistinctColorLongs
import com.example.learncompose.core.designsystem.generateDistinctColors
import kotlinx.coroutines.launch

data class GariItem(
    val id: Int = 0,
    val text: String = "",
    val color: Long = 0xFF9FEFFF,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(modifier: Modifier = Modifier) {
    val gridItems = remember {
        List(50) { index ->
            when (index) {
                0 -> {
                    GariItem(id = index, text = "短的")
                }

                1 -> {
                    GariItem(id = index, text = "短的短的")
                }

                2 -> {
                    GariItem(id = index, text = "中的中的中的")
                }

                3 -> {
                    GariItem(id = index, text = "中的中的中的中的")
                }

                else -> {
                    GariItem(id = index, text = "长的长的长的长的长的长的长的长的长的长的长的")
                }
            }
        }
    }
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var textState by remember { mutableStateOf("") }
    val colorList = generateDistinctColors(20)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        if (showBottomSheet) {
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
                        .padding(16.dp)
                ) {
                    Text(text = "颜色", Modifier.padding(bottom = 10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        items(count= colorList.size, key = { it }) { index ->
                            Box(
                                Modifier
                                    .padding(horizontal = 5.dp)
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(colorList[index])
                                    .border(1.dp, MaterialTheme.colorScheme.scrim, CircleShape)
                            ) {
                            }
                        }
                    }

                    Text(text = "习惯名", Modifier.padding(bottom = 10.dp))

                    BasicTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp) // 给输入框一个固定的高度
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer) // 自定义背景色
                            .padding(horizontal = 12.dp), // 左右内边距
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

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }
                    ) {
                        Text("确定")
                    }
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    }
                ) {
                    Icon(painterResource(R.drawable.lips_24px), contentDescription = "增加")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Compose",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {

                                }
                            }
                        ) {
                            Icon(painterResource(R.drawable.lips_24px), null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                            }
                        ) {
                            Icon(painter = painterResource(R.drawable.lips_24px), null)
                        }
                        // 2. 用 Box 作为锚点，确保菜单永远对齐这个按钮的右上角
                        Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {

                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.lips_24px),
                                    contentDescription = "用户菜单",
                                )
                            }

                            // 3. 高颜值定制化 DropdownMenu
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                // 通过 offset 让菜单向下微调，避免死死贴着顶栏，视觉上更轻盈
                                offset = DpOffset(x = (-8).dp, y = 4.dp),
                                modifier = Modifier.width(170.dp),
                                shape = RoundedCornerShape(16.dp),
                                shadowElevation = 8.dp,
                            ) {
                                // 菜单项 1：登出
                                DropdownMenuItem(
                                    modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                    text = {
                                        Text(
                                            "登出",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 15.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.lips_24px),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false // 点击后关闭
                                    }
                                )

                                // 分割线：增强视觉层次
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                )

                                // 菜单项 2：其他设置（示例）
                                DropdownMenuItem(
                                    modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                    text = {
                                        Text(
                                            "设置",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 15.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.lips_24px),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        // 在这里处理设置点击
                                    }
                                )
                            }
                        }
                    },
                )

                CompositionLocalProvider(
                    LocalCardScopeProvider provides rememberCoroutineScope()
                ) {
                    CardGrid(gridItems)
                }
            }

        }

    }
}

@Composable
fun CardGrid(gridItems: List<GariItem>) {
    val colorList = generateDistinctColorLongs(20)
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(60.dp),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = gridItems, key = { item -> item.id }) { item ->
            ScratchMaskCard(
                frontFaceContent = {
                    Column(Modifier.fillMaxSize(0.95f)) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            item.text,
                            Modifier.align(Alignment.CenterHorizontally),
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.weight(1f))
                    }
                },
                backFaceContent = {
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
                },
                frontFaceColor = MaterialTheme.colorScheme.surfaceDim,
                backFaceColor = Color(colorList[10])
            )
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