package com.example.learncompose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.learncompose.R
import com.example.learncompose.ui.theme.LearnComposeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    // 修复：移除 fillMaxSize 和 verticalScroll，避免与外层 HomeScreen 的滚动冲突
    FlowRow(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        var isShowDialog by remember { mutableStateOf(false) }
        Column(
            Modifier.width(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.search_24px),
                contentDescription = "card",
                Modifier.size(80.dp, 60.dp),
                contentScale = ContentScale.Fit,
                alpha = 0.8f,
                colorFilter = ColorFilter.tint(Color.DarkGray)
            )
            Text(
                text = "sdakljkljvxzlknvlkzxjfoijqofw",
                Modifier.padding(top = 5.dp),
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                painter = painterResource(R.drawable.check_circle_24px),
                contentDescription = "add",
            )
            Button(
                onClick = {
                    isShowDialog = true
                },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 50.dp
                ),
            ) {
                Icon(Icons.Default.Home, null)
                Spacer(Modifier.width(10.dp))
                Text("Home")
            }
        }
        var text by remember { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("用户名") },
            placeholder = { Text("请输入用户名" ) },
            leadingIcon = { Icon(painter = painterResource(R.drawable.favorite_24px), null) },
            trailingIcon = { Icon(painter = painterResource(R.drawable.menu_24px), null) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        if (isShowDialog) {
            Dialog(
                onDismissRequest = { isShowDialog = false }
            ) {
                Card {
                    Text("确定删除吗?", Modifier.padding(16.dp))
                }
            }
        }
        Checkbox(
            checked = true,
            onCheckedChange = {}
        )
        RadioButton(
            selected = true,
            onClick = {}
        )
        Switch(
            checked = true,
            onCheckedChange = {}
        )
        Slider(
            state = SliderState(0.5f)
        )
        val itemList = (1..100).toList()
        // 注意：LazyColumn 这里的 size 是固定的，所以不会引起测量报错
        LazyColumn(
            modifier = Modifier.size(100.dp, 200.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = rememberLazyListState()
        ) {
            items(itemList) { item ->
                Text(
                    text = "第${item}个"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
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
                    onClick = { /* 切换页面 */ },
                    icon = { Icon(painterResource(R.drawable.favorite_24px), null) },
                    label = { Text("收藏", fontSize = 16.sp) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("你点击了添加按钮！")
                    }
                }
            ) {
                Icon(painterResource(R.drawable.menu_24px), contentDescription = "增加")
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                text = "欢迎使用！这里的文字调大到了 22sp，看得清吗？",
                fontSize = 22.sp,
                lineHeight = 30.sp
            )

            Greeting() 
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LearnComposeTheme {
        HomeScreen()
    }
}
