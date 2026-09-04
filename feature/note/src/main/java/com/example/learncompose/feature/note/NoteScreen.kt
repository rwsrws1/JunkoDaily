package com.example.learncompose.feature.note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.learncompose.core.navigation.Navigator

@Composable
fun NoteScreen(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Red.copy(alpha = 0.5f))
        .clickable(
            onClick = onClick
        )
    )
}

@Preview
@Composable
private fun Preview() {
    NoteScreen()
}