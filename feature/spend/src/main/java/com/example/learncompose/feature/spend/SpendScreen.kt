package com.example.learncompose.feature.spend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SpendScreen(modifier: Modifier = Modifier) {
    Box(modifier
        .fillMaxSize()
        .background(Color.Blue.copy(alpha = 0.5f)))
}

@Preview
@Composable
private fun Preview() {
    SpendScreen()
}