package com.example.learncompose.features.welcome.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.learncompose.R
import com.example.learncompose.features.home.presentation.HomeContract
import com.example.learncompose.ui.theme.LearnComposeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun WelcomeScreen(
    onNavigateToLogin: (String) -> Unit = {},
    viewModel: WelcomeViewModel = viewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val width = LocalWindowInfo.current.containerSize.width.dp
    val height = LocalWindowInfo.current.containerSize.height.dp

    SnackbarHost(hostState = snackBarHostState)

    LaunchedEffect(Unit) {
        delay(1000.milliseconds)
        snackBarHostState.showSnackbar("width = $width, height = $height")
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is HomeContract.SideEffect.NavigationToDetail -> onNavigateToLogin(effect.id)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        val parentHeight = maxHeight
        when (windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                CombineContent(viewModel, parentHeight, 0)
            }

            WindowWidthSizeClass.MEDIUM -> {
                CombineContent(viewModel, parentHeight, 100)
            }

            WindowWidthSizeClass.EXPANDED -> {
                CombineContent(viewModel, parentHeight, 200)
            }
        }
    }
}

@Composable
fun CombineContent(viewModel: WelcomeViewModel, parentHeight: Dp, horizontalPadding: Int = 0) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Bottom
    )
    {
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(min = parentHeight + 1.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            MediumContent(
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            BottomContent(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding.dp)
                    .fillMaxWidth(),
                viewModel
            )
        }
    }
}

@Composable
fun MediumContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
}

@Composable
fun BottomContent(modifier: Modifier = Modifier, viewModel: WelcomeViewModel) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        Spacer(Modifier.size(10.dp))
        LoginButton("将账户添加至设备") {
            viewModel.handleIntent(HomeContract.Intent.ClickItem(state.items[0]))
        }
        LoginButton("保持已注销状态 ${state.testNumber}") {
            viewModel.handleIntent(HomeContract.Intent.PlusItem)
        }
        Spacer(Modifier.size(20.dp))
        val linkInteractionListener = LinkInteractionListener { annotation ->
            println("点击了${(annotation as LinkAnnotation.Clickable).tag}！")
        }
        val linkTextStyle = TextLinkStyles(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            )
        )
        val annotatedString = buildAnnotatedString {
            append("继续操作即表示您同意接受")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "terms",
                    // 设置正常状态下的样式，也可以设置 hover/focused 状态
                    styles = linkTextStyle,
                    linkInteractionListener
                )
            ) {
                append("服务条款")
            }
            append("。为了帮助改进这款应用程序，谷歌浏览器会将使用情况和崩溃数据发送给谷歌。")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "privacy",
                    styles = linkTextStyle,
                    linkInteractionListener
                )
            )
            {
                append("管理")
            }
            append("。")
        }

        Text(
            text = annotatedString,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
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

@PreviewScreenSizes
@Composable
fun Preview() {
    LearnComposeTheme {
        WelcomeScreen()
    }
}