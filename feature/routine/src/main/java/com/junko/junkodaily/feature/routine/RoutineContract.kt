package com.junko.junkodaily.feature.routine

import com.junko.junkodaily.core.model.RoutineCard
import com.junko.junkodaily.core.model.RoutineCardWithLog
import com.junko.junkodaily.core.model.RoutineDailyLog
import java.time.Instant
import java.time.LocalDate

class RoutineContract {
    data class UiState(
        val selectedDate: LocalDate = LocalDate.now(),
        val cardWithLogsMap: Map<LocalDate, List<RoutineCardWithLog>> = emptyMap(),
        val isLoading: Boolean = false
    )

    sealed interface Intent {
        data class SelectDate(val date: LocalDate) : Intent
        data class InsertCard(
            val cardText: String = "",
            val cardColor: Long = 0XFFFFFFFF,
            val cardImage: String = "",
            val cardShape: String = ""
        ) : Intent
        data class DeleteCardById(val cardId: Long) : Intent
        data class UpsertDailyLog(
            val cardId: Long = 0,
            val recordDate: LocalDate,
            val isCompleted: Boolean = false,
            val completedAt: Instant,
        ) : Intent
    }
}
