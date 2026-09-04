package com.example.learncompose.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import com.example.learncompose.main.MainScreen

@Composable
fun AppScreen(splashScreen: SplashScreen) {
    splashScreen.setKeepOnScreenCondition {
        false
    }
    MainScreen()
}