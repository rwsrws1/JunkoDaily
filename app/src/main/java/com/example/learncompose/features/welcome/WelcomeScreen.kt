package com.example.learncompose.features.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.window.core.layout.WindowSizeClass
import com.example.learncompose.R
import com.example.learncompose.core.designsystem.LearnComposeTheme
import com.example.learncompose.core.designsystem.components.ButtonPrimary
import kotlinx.coroutines.launch

val LocalWelcomeIntentHandler = staticCompositionLocalOf<(WelcomeContract.Intent) -> Unit> {
    {}
}

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = viewModel(),
    onNavigateToMain: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val width = LocalWindowInfo.current.containerSize.width.dp
    val height = LocalWindowInfo.current.containerSize.height.dp

    val intentHandler: (WelcomeContract.Intent) -> Unit = { intent ->
        when (intent) {
            is WelcomeContract.Intent.ShowMessage -> {
                scope.launch {
                    snackBarHostState.showSnackbar(
                        intent.message,
                        actionLabel = "确定",             // 可选：你的行动按钮
                        withDismissAction = true,         // 核心：强制开启右侧的关闭“叉叉”
                        duration = SnackbarDuration.Short // 弹出时长
                    )
                }
            }
            is WelcomeContract.Intent.ViewModelIntent -> {
                viewModel.handleIntent(intent)
            }
        }
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is WelcomeContract.SideEffect.LoginAsVisitor -> onNavigateToMain(effect.id)
            }
        }
    }

    CompositionLocalProvider(LocalWelcomeIntentHandler provides intentHandler) {
        Box(modifier = Modifier.fillMaxSize()) {
            CombineContent(state)
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).statusBarsPadding()
            ) { snackBarData ->
                Snackbar(
                    snackbarData = snackBarData,
//                    containerColor = Color(0xFFE53935), // 背景改成姨妈红
//                    contentColor = Color.White,         // 文字改成纯白
//                    actionColor = Color.Yellow,         // 按钮文字改成黄色
                    shape = RoundedCornerShape(16.dp),  // 变成大圆角
                    // dismissActionContentColor = ...  // 右侧关闭叉叉的颜色
                )
            }
        }
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
        val horizontalPadding = if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            200
        } else if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            100
        } else {
            0
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
            painter = painterResource(R.drawable.menu_book_24px),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = "Jetpack Compose 从入门到入土",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = "进入即可了解全新的声明式UI开发框架，快赶在谷歌废弃它之前学会把！",
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
        ButtonPrimary(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            onClick = {
            intentHandler(WelcomeContract.Intent.ClickEnter(state.items[0]))
        }) {
            Text("开始学习")
        }
        ButtonPrimary(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            isEnabled = state.testNumber < 10086,
            isPressOnClick = true,
            onClick = {
            intentHandler(WelcomeContract.Intent.PlusItem)
        }) {
            Text(text = "学不动了 +${state.testNumber}")
        }
        Spacer(Modifier.size(20.dp))
        val linkInteractionListener = LinkInteractionListener { annotation ->
            val tag = (annotation as LinkAnnotation.Clickable).tag
            intentHandler(WelcomeContract.Intent.ShowMessage(
                "点击了$tag！"
            ))
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
            append("。为了帮助改进这款应用程序，该应用会将使用情况和崩溃数据发送给谷歌。")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "拒绝",
                    styles = linkTextStyle,
                    linkInteractionListener
                )
            )
            {
                append("拒绝")
            }
        }

        Text(
            text = annotatedString,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@PreviewScreenSizes
@Composable
private fun Preview() {
    LearnComposeTheme {
        CombineContent()
    }
}