package com.example.learncompose.feature.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RoutineScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Green.copy(alpha = 0.5f)))
}

@Preview
@Composable
private fun Preview() {
    RoutineScreen()
}