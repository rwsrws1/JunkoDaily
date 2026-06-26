package com.example.learncompose.features.login.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.learncompose.R
import com.example.learncompose.data.repository.AuthState
import com.example.learncompose.ui.theme.LearnComposeTheme

val LocalLoginIntentHandler = staticCompositionLocalOf<(LoginContract.Intent) -> Unit> {
    {}
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {}
) {

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is LoginContract.SideEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    CompositionLocalProvider(LocalLoginIntentHandler provides viewModel::handleIntent) {
        CombineContent()
    }
}

@Composable
fun CombineContent() {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .padding(20.dp)
    ) {
        val parentHeight = maxHeight
        var bottomPadding = 0
        when (windowSizeClass.windowHeightSizeClass) {
            WindowHeightSizeClass.COMPACT -> {
                bottomPadding = 0
            }
            WindowHeightSizeClass.MEDIUM -> {
                bottomPadding = 100
            }
            WindowHeightSizeClass.EXPANDED -> {
                bottomPadding = 200
            }
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
                Spacer(Modifier.weight(1f))
                MediumContent()
                Spacer(Modifier.weight(2f))
                BottomContent()
                Spacer(Modifier.height(bottomPadding.dp))
            }
        }
    }
}

@Composable
fun MediumContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var account by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
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
            println("点击了${(annotation as LinkAnnotation.Clickable).tag}！")
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
                    tag = "phone",
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
    }
}

@Composable
fun BottomContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val intentHandler = LocalLoginIntentHandler.current
        Image(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .clickable(
                    onClick = {
                        intentHandler(LoginContract.Intent.ClickLogin())
                    }
                )
                .padding(15.dp),
            painter = painterResource(R.drawable.login_24px),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        )
    }
}

@PreviewScreenSizes
@Composable
fun LoginScreenPreview() {
    LearnComposeTheme {
        CombineContent()
    }
}