package com.example.learncompose.features.welcome.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learncompose.R
import com.example.learncompose.features.home.presentation.HomeContract
import com.example.learncompose.ui.theme.LearnComposeTheme

@Composable
fun WelcomeScreen(
    onNavigateToLogin: (String) -> Unit = {},
    viewModel: WelcomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is HomeContract.SideEffect.NavigationToDetail -> onNavigateToLogin(effect.id)
            }
        }
    }

    Box(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 20.dp)) {
        Column(
            Modifier.align(Alignment.Center).offset(y = (-100).dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(R.drawable.forum_24px),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = "让 Chrome 符合你的需求",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = "登录即可在所有设备上获取您的书签、密码及其他内容。",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        Column(
            Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.size(10.dp))
            LoginButton("将账户添加至设备") {
                viewModel.handleIntent(HomeContract.Intent.ClickItem(state.items[0]))
            }
            LoginButton("保持已注销状态")
            Spacer(Modifier.size(20.dp))
            Text(
                text = "继续操作即表示您同意接受服务条款。为了帮助改进这款应用程序，谷歌浏览器会将使用情况和崩溃数据发送给谷歌。管理",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoginButton(text: String = "", onclick: () -> Unit = {}) {
    Button(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        onClick = onclick
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun Preview(modifier: Modifier = Modifier) {
    LearnComposeTheme {
        WelcomeScreen()
    }
}