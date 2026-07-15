package com.example.learncompose.features.home

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
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupPositionProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.example.learncompose.R
import com.example.learncompose.features.home.navigation.HomeNavGraph
import com.example.learncompose.features.home.navigation.HomeNavKey
import com.example.learncompose.ui.screen.AvatarSelector
import com.example.learncompose.ui.theme.LearnComposeTheme
import kotlinx.coroutines.launch

val localHomeHandler = staticCompositionLocalOf<(HomeContract.Intent) -> Unit> { {} }

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
) {

    val currentKey by viewModel.currentKey.collectAsStateWithLifecycle()

//    LaunchedEffect(viewModel.sideEffect) {
//        viewModel.sideEffect.collect { effect ->
//            when (effect) {
//                is HomeContract.SideEffect.NavigateToLogin -> onNavigateToLogin()
//            }
//        }
//    }

    CompositionLocalProvider(localHomeHandler provides viewModel::handleIntent) {
        CombineScreen(currentKey)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombineScreen(currentKey: HomeNavKey = HomeNavKey.Greeting) {
    val intentHandler = localHomeHandler.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

//    var currentKey: HomeNavKey by remember { mutableStateOf(HomeNavKey.Greeting) }
    val homeNaviKeys = listOf(HomeNavKey.Greeting, HomeNavKey.Statistics, HomeNavKey.Profile)
    val stacks = homeNaviKeys.associateWith { key ->
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
                        Text(
                            text = "Compose",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
//                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                ),
                    navigationIcon = {
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
                    },
                    actions = {

                        // 1. 定义控制菜单展开的状态
                        var menuExpanded by remember { mutableStateOf(false) }

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
                                        intentHandler(HomeContract.Intent.UserInfo) // 触发原有逻辑
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

                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                NavigationBar {
                    homeNaviKeys.forEach { key ->
                        var painterResource: Painter = painterResource(R.drawable.home_24px)
                        var textContent: String = ""

                        when (key) {
                            is HomeNavKey.Greeting -> {
                                textContent = "首页"
                                painterResource = if (currentKey == key ) {
                                    painterResource(R.drawable.home_24px_filled)
                                } else {
                                    painterResource(R.drawable.home_24px)
                                }
                            }
                            is HomeNavKey.Statistics -> {
                                textContent = "统计"
                                painterResource = if (currentKey == key ) {
                                    painterResource(R.drawable.insert_chart_24px_filled)
                                } else {
                                    painterResource(R.drawable.insert_chart_24px)
                                }
                            }
                            is HomeNavKey.Profile -> {
                                textContent = "我的"
                                painterResource = if (currentKey == key ) {
                                    painterResource(R.drawable.person_24px_filled)
                                } else {
                                    painterResource(R.drawable.person_24px)
                                }
                            }
                            else -> {}
                        }
                        NavigationBarItem(
                            selected = currentKey == key,
                            onClick = {
//                                currentKey = key
                                intentHandler(HomeContract.Intent.ChangeCurrentKey(key))
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
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(5.dp),
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
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
                    HomeNavGraph(currentStack)
                }
            }


        }


    }


}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LearnComposeTheme {
        // NavDisplay internally uses NavigationBackHandler which requires LocalNavigationEventDispatcherOwner.
        // In Previews, we need to provide a root dispatcher manually.
        val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)
        CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
            CombineScreen()
        }
    }
}