package com.example.learncompose.feature.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.learncompose.core.designsystem.icons.AppIcons

@Composable
fun NoteDetailScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Yellow.copy(alpha = 0.5f))
    ) {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(AppIcons.note),
                contentDescription = ""
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NoteDetailScreen()
}