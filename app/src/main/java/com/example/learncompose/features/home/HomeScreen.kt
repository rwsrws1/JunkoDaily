package com.example.learncompose.features.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.learncompose.R
import com.example.learncompose.features.home.navigation.HomeNavGraph
import com.example.learncompose.features.home.navigation.HomeNavKey
import com.example.learncompose.ui.components.GreetingScreen
import com.example.learncompose.ui.screen.AvatarSelector
import com.example.learncompose.ui.theme.LearnComposeTheme
import kotlinx.coroutines.launch

val localHomeHandler = staticCompositionLocalOf<(HomeContract.Intent) -> Unit> { {} }

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
) {

//    LaunchedEffect(viewModel.sideEffect) {
//        viewModel.sideEffect.collect { effect ->
//            when (effect) {
//                is HomeContract.SideEffect.NavigateToLogin -> onNavigateToLogin()
//            }
//        }
//    }

    CompositionLocalProvider(localHomeHandler provides viewModel::handleIntent) {
        CombineScreen()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombineScreen(modifier: Modifier = Modifier) {
    val intentHandler = localHomeHandler.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val selectedOptionsLabel = listOf("首页", "统计", "我的")
    val rememberNavBackStack = rememberNavBackStack(HomeNavKey.Greeting)

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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    AvatarSelector(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 4.dp) // 离边缘留点空隙
                            .size(36.dp)       // 在顶栏里 36dp 看起来非常精致高级
                    )
                },
                actions = {

                    // 1. 定义控制菜单展开的状态
                    var menuExpanded by remember { mutableStateOf(false) }

                    // 2. 用 Box 作为锚点，确保菜单永远对齐这个按钮的右上角
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {

                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "用户菜单",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        // 3. 高颜值定制化 DropdownMenu
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            // 通过 offset 让菜单向下微调，避免死死贴着顶栏，视觉上更轻盈
                            offset = DpOffset(x = (-8).dp, y = 4.dp),
                            modifier = Modifier
                                .width(170.dp)
                                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)) // 增加柔和阴影
                                .clip(RoundedCornerShape(16.dp)) // 大圆角，更有现代高级感
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh) // 使用 M3 容器色，拒绝死白
                        ) {
                            // 菜单项 1：登出
                            DropdownMenuItem(
                                text = { Text("登出", fontWeight = FontWeight.Medium, fontSize = 15.sp) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.logout_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
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
                                text = { Text("设置", fontWeight = FontWeight.Medium, fontSize = 15.sp) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.settings_24px_filled),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                selectedOptionsLabel.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            rememberNavBackStack.clear()
                            rememberNavBackStack.add(
                                when (index) {
                                    0 -> HomeNavKey.Greeting
                                    1 -> HomeNavKey.CharDemo
                                    2 -> HomeNavKey.MediaPiker
                                    else -> HomeNavKey.Greeting
                                }
                            )
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    Badge()
                                }) {
                                when (label) {
                                    "首页" -> Icon(painterResource(R.drawable.home_24px_filled), contentDescription = null)
                                    "统计" -> Icon(painterResource(R.drawable.bar_chart_4_bars_24px), contentDescription = null)
                                    "我的" -> Icon(painterResource(R.drawable.person_24px), contentDescription = null)
                                }
                            }
                        },
                        label = { Text(label, fontSize = 16.sp) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                }
            ) {
                Icon(painterResource(R.drawable.add_24px), contentDescription = "增加")
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

//            AnimatedVisibility(
//                visible = rememberVisibility,
//                enter = slideInVertically(animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing))
//            ) {
//                Greeting()
//            }

            HomeNavGraph(rememberNavBackStack)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LearnComposeTheme {
        CombineScreen()
    }
}