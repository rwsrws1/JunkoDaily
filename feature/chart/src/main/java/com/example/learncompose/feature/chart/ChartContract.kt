package com.example.learncompose.feature.routine.chart

import com.example.learncompose.core.model.RoutineCardsAndLogs

class ChartContract {
    data class UiState(
        val cardsAndLogs: List<RoutineCardsAndLogs> = emptyList(),
        val isLoading: Boolean = false
    )
}