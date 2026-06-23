package com.example.learncompose.features.welcome.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Box(Modifier.fillMaxSize().background(color = Color.White), contentAlignment = Alignment.Center) {
        Column(Modifier
            .size(100.dp, 100.dp)
            .background(color = Color.LightGray),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                viewModel.handleIntent(HomeContract.Intent.ClickItem(state.items[0]))
            }) {
                Text(text = "欢迎")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview(modifier: Modifier = Modifier) {
    LearnComposeTheme {
        WelcomeScreen()
    }
}