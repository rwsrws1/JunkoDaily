package com.example.learncompose.feature.routine.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learncompose.core.designsystem.components.TopBarPrimary
import com.example.learncompose.feature.routine.composeColor
import java.time.YearMonth

@Composable
fun RoutineChartViewModelScreen(modifier: Modifier = Modifier, viewModel: RoutineChartViewModel, onNavigationClick: () -> Unit = {}) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RoutineChartScreen(modifier, uiState, onNavigationClick)
}

@Composable
fun RoutineChartScreen(modifier: Modifier = Modifier, uiState: RoutineChartContract.UiState = RoutineChartContract.UiState(), onNavigationClick: () -> Unit = {}) {
    val cardsAndLogs = uiState.cardsAndLogs
    val currentMonth = 9

    val daysInMonths: List<Int> = (1..12).map { month ->
        YearMonth.of(2026, month).lengthOfMonth()
    }

    Box(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background))
    {
        Column(Modifier.fillMaxSize()) {
            TopBarPrimary(onNavigationClick = onNavigationClick)
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Adaptive(150.dp),
                state = rememberLazyGridState(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(count = cardsAndLogs.size, key = { it }) { index ->
                    val completedDays = cardsAndLogs[index].logs.filter {
                        it.recordDate?.monthValue == currentMonth
                    }.filter {
                        it.isCompleted
                    }.map {
                        it.recordDate?.dayOfMonth
                    }
                    Card(Modifier
                        .aspectRatio(1f / 1f)
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface, shape = MaterialTheme.shapes.medium),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Spacer(Modifier.height(10.dp))
                        Text(cardsAndLogs[index].card.cardText, Modifier.align(Alignment.CenterHorizontally))
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = GridCells.Fixed(7),
                            contentPadding = PaddingValues(10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            item {
                                Box(Modifier
                                    .aspectRatio(1f / 1f)
                                    .background(
                                        Color.Transparent
                                    ))
                            }
                            item {
                                Box(Modifier
                                    .aspectRatio(1f / 1f)
                                    .background(
                                        Color.Transparent
                                    ))
                            }
                            items(count = daysInMonths[9], key = { it }) { day ->
                                DayBox(
                                    color = if (completedDays.contains(day)) cardsAndLogs[index].card.composeColor
                                    else MaterialTheme.colorScheme.surfaceContainerHighest,
                                    number = day
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayBox(modifier: Modifier = Modifier, color: Color, number: Int) {
    Box(modifier
        .aspectRatio(1f / 1f)
        .clip(MaterialTheme.shapes.extraSmall)
        .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text("$number", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.surfaceContainerLowest)
    }
}

@Preview
@Composable
private fun Preview() {
    RoutineChartScreen()
}