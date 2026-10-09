package com.junko.junkodaily.feature.experiment.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.junko.junkodaily.feature.experiment.R
import com.junko.junkodaily.feature.experiment.SearchComponent
import com.junko.junkodaily.core.designsystem.theme.AppTheme
import com.junko.junkodaily.feature.experiment.main.navigation.ExperimentNavGraph
import com.junko.junkodaily.feature.experiment.main.navigation.ExperimentNavKey
import com.junko.junkodaily.feature.experiment.AvatarSelector
import kotlinx.coroutines.launch

val localExperimentHandler = staticCompositionLocalOf<(ExperimentContract.Intent) -> Unit> { {} }

@Composable
fun ExperimentScreen(
    viewModel: ExperimentModel = hiltViewModel<ExperimentModel>(),
    onNavigateToLoading: () -> Unit = {}
) {

    val currentKey by viewModel.currentKey.collectAsStateWithLifecycle()

    CompositionLocalProvider(localExperimentHandler provides viewModel::handleIntent) {
        CombineScreen(currentKey, onNavigateToLoading)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombineScreen(currentKey: ExperimentNavKey = ExperimentNavKey.Experiment, onNavigateToLoading: () -> Unit = {}) {
    val intentHandler = localExperimentHandler.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    var searchState by rememberSaveable { mutableStateOf(false) }
    val textFieldState: TextFieldState = rememberTextFieldState()
    val onSearch: (String) -> Unit = {
        searchState = false
    }
    val searchResults: List<String> = listOf("111", "222", "333")
    val focusRequester = remember { FocusRequester() }

    val naviKeys = listOf(ExperimentNavKey.Experiment, ExperimentNavKey.Note, ExperimentNavKey.Habit, ExperimentNavKey.Spend)
    val stacks = naviKeys.associateWith { key ->
        rememberNavBackStack(key)
    }
    val currentStack = stacks[currentKey]!!
    val savableStateHolder = rememberSaveableStateHolder()

    ModalNavigationDrawer (
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                AvatarSelector(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 4.dp)
                        .size(36.dp)
                )
                HorizontalDivider()
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {

                TopAppBar(
                    title = {
                        if (searchState) {
                            ProvideTextStyle(
                                value = MaterialTheme.typography.bodyMedium
                            ) {
                                SearchComponent(
                                    modifier = Modifier.padding(end = 16.dp),
                                    textFieldState = textFieldState,
                                    onSearch = onSearch,
                                    searchResults = searchResults,
                                    focusRequester = focusRequester,
                                    trailingIconId = R.drawable.search_24px
                                )
                            }

                            SideEffect {
                                focusRequester.requestFocus()
                            }
                        } else {
                            Text(
                                text = "Compose",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
//                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                ),
                    navigationIcon = {
                        if (!searchState) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                }
                            ) {
                                Icon(painterResource(R.drawable.face_24px), null)
                            }
                        }
                    },
                    actions = {
                        if (!searchState) {
                            IconButton(
                                onClick = {
                                    searchState = true
                                }
                            ) {
                                Icon(painter = painterResource(R.drawable.search_24px), null)
                            }
                            // 2. 用 Box 作为锚点，确保菜单永远对齐这个按钮的右上角
                            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {

                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.menu_24px),
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
//                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ) {
                                    // 菜单项 1：登出
                                    DropdownMenuItem(
                                        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                        text = { Text("登出", fontWeight = FontWeight.Medium, fontSize = 15.sp) },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.logout_24px),
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false // 点击后关闭
                                            intentHandler(ExperimentContract.Intent.Logout) // 触发原有逻辑
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
                                        text = { Text("设置", fontWeight = FontWeight.Medium, fontSize = 15.sp) },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.settings_24px_filled),
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
                        }
                    },
                    scrollBehavior = scrollBehavior
                )

            },
            bottomBar = {
                NavigationBar {
                    naviKeys.forEach { key ->
                        var painterResource: Painter = painterResource(R.drawable.home_24px)
                        var textContent: String = ""

                        when (key) {
                            is ExperimentNavKey.Experiment -> {
                                textContent = "实验页"
                                painterResource = if (currentKey == key ) {
                                    painterResource(R.drawable.home_24px_filled)
                                } else {
                                    painterResource(R.drawable.home_24px)
                                }
                            }
                            is ExperimentNavKey.Note -> {
                                textContent = "便签"
                                painterResource = if (currentKey == key ) {
                                    painterResource(R.drawable.widgets_24px_filled)
                                } else {
                                    painterResource(R.drawable.widgets_24px)
                                }
                            }
                            is ExperimentNavKey.Habit -> {
                                textContent = "习惯"
                                painterResource = if (currentKey == key ) {
                                    painterResource(R.drawable.person_24px_filled)
                                } else {
                                    painterResource(R.drawable.person_24px)
                                }
                            }
                            is ExperimentNavKey.Spend -> {
                                textContent = "开销"
                                painterResource = if (currentKey == key ) {
                                    painterResource(R.drawable.filter_alt_24px_filled)
                                } else {
                                    painterResource(R.drawable.filter_alt_24px)
                                }
                            }
                            else -> {}
                        }
                        NavigationBarItem(
                            selected = currentKey == key,
                            onClick = {
                                intentHandler(ExperimentContract.Intent.ChangeCurrentKey(key))
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        Badge()
                                    }) {
                                    Icon(
                                        painter = painterResource,
                                        contentDescription = null
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = textContent,
                                    fontSize = 16.sp
                                )
                            }
                        )
                    }
                }
            },
            floatingActionButton = {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above,
                        5.dp
                    ),
                    state = rememberTooltipState(),
                    tooltip = {
                        PlainTooltip() {
                            Text("提示")
                        }
                    }
                ) {
                    FloatingActionButton(
                        onClick = {
                            showBottomSheet = true
                        }
                    ) {
                        Icon(painterResource(R.drawable.add_24px), contentDescription = "增加")
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                                .fillMaxHeight(0.8f)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "底部面板内容",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

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
                                Text("隐藏面板")
                            }
                        }
                    }
                }

                savableStateHolder.SaveableStateProvider(currentKey.toString()) {
                    ExperimentNavGraph(currentStack, onNavigateToLoading)
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)
        CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
            CombineScreen()
        }
    }
}