package com.example.learncompose.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.learncompose.R
import com.example.learncompose.data.repo.AuthState
import com.example.learncompose.ui.components.Loading
import com.example.learncompose.ui.components.ButtonPrimary
import com.example.learncompose.ui.theme.LearnComposeTheme

val LocalLoginIntentHandler = staticCompositionLocalOf<(LoginContract.Intent) -> Unit> {
    {}
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is LoginContract.SideEffect.NavigateToMain -> onNavigateToMain()
            }
        }
    }

    CompositionLocalProvider(LocalLoginIntentHandler provides viewModel::handleIntent) {
        CombineContent(state)
    }
}

@Composable
fun CombineContent(state: LoginContract.State = LoginContract.State()) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .padding(20.dp)
    ) {
        val parentHeight = maxHeight
        val bottomPadding = if (windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)) {
            200
        } else if (windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
            100
        } else {
            0
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = parentHeight + 1.dp)
            ) {
                var account by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }
                Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "手机号登录",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.size(50.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = account,
                        onValueChange = { account = it },
                        label = { Text(text = "账号") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(Modifier.size(20.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "密码") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                    Spacer(Modifier.size(20.dp))
                    Text(
                        modifier = Modifier.align(Alignment.Start),
                        text = "上述手机号仅用于登录验证",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.size(20.dp))
                    val linkInteractionListener = LinkInteractionListener { annotation ->
                        val tag = (annotation as LinkAnnotation.Clickable).tag
                    }
                    val linkTextStyle = TextLinkStyles(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.None,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    val annotatedString = buildAnnotatedString {
                        append("")
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "使用其他方式登录",
                                styles = linkTextStyle,
                                linkInteractionListener = linkInteractionListener
                            )
                        ) {
                            append("使用其他方式登录")
                        }
                    }
                    Text(
                        modifier = Modifier.align(Alignment.Start),
                        text = annotatedString,
                        style = MaterialTheme.typography.bodySmall
                    )

//                    TextButton(
//                        modifier = Modifier.align(Alignment.Start),
//                        onClick = {}
//                    ) {
//                        Text("不用了")
//                    }
                }

                Spacer(Modifier.weight(2f))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val intentHandler = LocalLoginIntentHandler.current

                    ButtonPrimary(
                        modifier = Modifier.size(80.dp),
                        onClick = {
                            intentHandler(LoginContract.Intent.ClickLogin(
                                account = account,
                                password = password
                            ))
                        }
                    ) {
                        Image(
                            modifier = Modifier.size(36.dp),
                            painter = painterResource(R.drawable.login_24px),
                            contentDescription = "登录",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }

                Spacer(Modifier.height(bottomPadding.dp))
            }
        }
    }

    if (state.authState is AuthState.Loading) {
        Loading()
    }
}

@PreviewScreenSizes
@Composable
private fun Preview() {
    LearnComposeTheme {
        CombineContent(LoginContract.State(authState = AuthState.LoggedOut))
    }
}