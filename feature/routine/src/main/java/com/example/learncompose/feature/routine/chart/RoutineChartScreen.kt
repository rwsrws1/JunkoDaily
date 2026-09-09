package com.example.learncompose.feature.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.learncompose.core.designsystem.components.TopBarPrimary

@Composable
fun RoutineChartScreen(modifier: Modifier = Modifier, onNavigationClick: () -> Unit = {}) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer)) {
        TopBarPrimary(onNavigationClick = onNavigationClick)
    }
}