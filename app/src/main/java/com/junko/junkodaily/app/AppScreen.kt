package com.junko.junkodaily.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import com.junko.junkodaily.main.MainScreen

@Composable
fun AppScreen(splashScreen: SplashScreen) {
    splashScreen.setKeepOnScreenCondition {
        false
    }
    MainScreen()
}