package com.junko.junkodaily.feature.chart

import com.junko.junkodaily.core.model.RoutineCardsAndLogs

class ChartContract {
    data class UiState(
        val cardsAndLogs: List<RoutineCardsAndLogs> = listOf(),
        val isLoading: Boolean = false
    )
}