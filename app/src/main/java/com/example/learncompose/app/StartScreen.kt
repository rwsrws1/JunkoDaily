package com.example.learncompose.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.learncompose.common.CommonScreen
import com.example.learncompose.navigation.AppNavGraph

@Composable
fun StartScreen(modifier: Modifier = Modifier) {
    val mainViewModel: StartViewModel = hiltViewModel()
    val startDestination = mainViewModel.startDestination

    if (startDestination == null) {
        CommonScreen()
    } else {
        AppNavGraph(startDestination = startDestination)
    }
}