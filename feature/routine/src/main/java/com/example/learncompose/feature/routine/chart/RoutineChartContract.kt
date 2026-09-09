package com.example.learncompose.feature.routine.chart

import com.example.learncompose.core.model.RoutineCardsAndLogs

class RoutineChartContract {
    data class UiState(
        val cardsAndLogs: List<RoutineCardsAndLogs> = listOf(RoutineCardsAndLogs())
    )
}