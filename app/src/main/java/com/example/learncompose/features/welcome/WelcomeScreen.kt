package com.example.learncompose.features.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.learncompose.R
import com.example.learncompose.ui.theme.LearnComposeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

val LocalWelcomeIntentHandler = staticCompositionLocalOf<(WelcomeContract.Intent) -> Unit> {
    {}
}

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = viewModel(),
    onNavigateToLogin: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    val width = LocalWindowInfo.current.containerSize.width.dp
    val height = LocalWindowInfo.current.containerSize.height.dp

    SnackbarHost(hostState = snackBarHostState)

    LaunchedEffect(Unit) {
        delay(1000.milliseconds)
        snackBarHostState.showSnackbar("width = $width, height = $height")
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is WelcomeContract.SideEffect.NavigateToLogin -> onNavigateToLogin(effect.id)
            }
        }
    }

    CompositionLocalProvider(LocalWelcomeIntentHandler provides viewModel::handleIntent) {
        CombineContent(state)
    }
}

@Composable
fun CombineContent(state: WelcomeContract.State = WelcomeContract.State()) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .padding(20.dp)
    ) {
        val parentHeight = maxHeight
        var horizontalPadding = 0
        when (windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                horizontalPadding = 0
            }

            WindowWidthSizeClass.MEDIUM -> {
                horizontalPadding = 100
            }

            WindowWidthSizeClass.EXPANDED -> {
                horizontalPadding = 200
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = parentHeight + 1.dp)
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
                    state
                )
            }
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
fun BottomContent(modifier: Modifier = Modifier, state: WelcomeContract.State) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val intentHandler = LocalWelcomeIntentHandler.current
        Spacer(Modifier.size(10.dp))
        LoginButton("将账户添加至设备") {
            intentHandler(WelcomeContract.Intent.ClickLogin(state.items[0]))
        }
        LoginButton("保持已注销状态 ${state.testNumber}") {
            intentHandler(WelcomeContract.Intent.PlusItem)
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
                    tag = "服务条款",
                    styles = linkTextStyle,
                    linkInteractionListener
                )
            ) {
                append("服务条款")
            }
            append("。为了帮助改进这款应用程序，谷歌浏览器会将使用情况和崩溃数据发送给谷歌。")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "管理",
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
        CombineContent()
    }
}