package com.junko.junkodaily.app

import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.junko.junkodaily.navigation.AppNavGraph
import com.junko.junkodaily.oldLogic.AppViewModel

@Composable
fun OldAppScreen(splashScreen: SplashScreen) {
    val appViewModel: AppViewModel = hiltViewModel()
    val startDestination = appViewModel.startDestination
    splashScreen.setKeepOnScreenCondition {
        startDestination == null
    }
    startDestination?.also {
        AppNavGraph(startDestination = it)
    }
}