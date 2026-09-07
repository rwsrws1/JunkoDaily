package com.example.learncompose.feature.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learncompose.core.designsystem.theme.AppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.Modifier

@Composable
fun NoteScreen(modifier: Modifier = Modifier, onClick: () -> Unit = {}, toExperiment: () -> Unit = {}) {

    Box(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(50.dp))
            StaggeredCardGrid(onClick, toExperiment)
        }
    }
}

@Composable
fun StaggeredCardGrid(onClick: () -> Unit = {}, toExperiment: () -> Unit = {}) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(70.dp),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }, key = "button_Button_1") {
            Button(onClick = toExperiment, enabled = false) {
                Text("go to experiment")
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }, key = "button_Button_2") {
            Button(onClick = onClick) {
                Text("go to next page")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        NoteScreen()
    }
}