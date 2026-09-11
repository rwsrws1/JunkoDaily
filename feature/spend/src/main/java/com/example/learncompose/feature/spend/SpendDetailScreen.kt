package com.example.learncompose.feature.spend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.learncompose.core.designsystem.icons.AppIcons

@Composable
fun SpendDetailScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Yellow.copy(alpha = 0.5f))
        .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(AppIcons.chart),
                contentDescription = ""
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SpendDetailScreen()
}