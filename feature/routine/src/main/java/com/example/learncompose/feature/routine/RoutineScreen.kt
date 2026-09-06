package com.example.learncompose.feature.routine

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.learncompose.core.designsystem.components.FlipCard
import com.example.learncompose.core.designsystem.components.card.LocalCardScopeProvider
import com.example.learncompose.core.designsystem.components.card.ScratchMaskCard
import com.example.learncompose.core.designsystem.generateDistinctColorLongs

data class GariItem(
    val id: Int = 0,
    val text: String = "",
    val color: Long = 0xFF9FEFFF,
)


@Composable
fun RoutineScreen(modifier: Modifier = Modifier) {

    val gridItems = remember {
        List(50) { index ->
            when (index) {
                0 -> {
                    GariItem(id = index, text = "短的")
                }
                1 -> {
                    GariItem(id = index, text = "短的短的")
                }
                2 -> {
                    GariItem(id = index, text = "中的中的中的")
                }
                3 -> {
                    GariItem(id = index, text = "中的中的中的中的")
                }
                else -> {
                    GariItem(id = index, text = "长的长的长的长的长的长的长的长的长的长的长的")
                }
            }
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(50.dp))
            CompositionLocalProvider(
                LocalCardScopeProvider provides rememberCoroutineScope()
            ) {
                StaggeredCardGrid(gridItems)
            }
        }
    }
}

@Composable
fun StaggeredCardGrid(gridItems: List<GariItem>) {
    val colorList = generateDistinctColorLongs(20)
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(60.dp),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = gridItems, key = { item -> item.id }) { item ->
            ScratchMaskCard(
                frontFaceContent = {
                    Column(Modifier.fillMaxSize(0.95f)) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            item.text,
                            Modifier.align(Alignment.CenterHorizontally),
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.weight(1f))
                    }
                },
                backFaceContent = {
                    Column(Modifier.fillMaxSize(0.95f)) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            item.text,
                            Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally),
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.weight(1f))
                    }
                },
                frontFaceColor = MaterialTheme.colorScheme.surfaceDim,
                backFaceColor = Color(colorList[10])
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        RoutineScreen()
    }
}