package com.example.learncompose.feature.chart

import com.example.learncompose.core.model.RoutineCardsAndLogs

class ChartContract {
    data class UiState(
        val cardsAndLogs: List<RoutineCardsAndLogs> = listOf(),
        val isLoading: Boolean = false
    )
}