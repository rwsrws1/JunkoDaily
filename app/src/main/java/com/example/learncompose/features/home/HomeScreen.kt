package com.example.learncompose.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.learncompose.R
import com.example.learncompose.ui.components.Greeting
import com.example.learncompose.ui.theme.LearnComposeTheme
import kotlinx.coroutines.launch

val localHomeHandler = staticCompositionLocalOf<(HomeContract.Intent) -> Unit> { {} }

@Composable
fun HomeScreen() {
    val viewmodel = hiltViewModel<HomeViewModel>()
    CompositionLocalProvider(localHomeHandler provides viewmodel::handleIntent) {
        CombineScreen()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombineScreen(modifier: Modifier = Modifier) {
    val intentHandler = localHomeHandler.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "我的学习应用",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* 切换页面 */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("首页", fontSize = 16.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        intentHandler(HomeContract.Intent.Logout)
                    },
                    icon = { Icon(painterResource(R.drawable.favorite_24px), null) },
                    label = { Text("登出", fontSize = 16.sp) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        snackBarHostState.showSnackbar("你点击了添加按钮！")
                    }
                }
            ) {
                Icon(painterResource(R.drawable.menu_24px), contentDescription = "增加")
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // 整体内容滚动
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.line_1),
                fontSize = 22.sp,
                lineHeight = 30.sp
            )
            Greeting()
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