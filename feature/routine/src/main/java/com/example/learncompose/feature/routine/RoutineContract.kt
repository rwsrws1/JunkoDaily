package com.example.learncompose.feature.routine

import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardWithLog
import com.example.learncompose.core.model.RoutineDailyLog
import java.time.LocalDate

class RoutineContract {
    data class UiState(
        val selectedDate: LocalDate = LocalDate.now(),
        val cardWithLogsMap: Map<LocalDate, List<RoutineCardWithLog>> = emptyMap(),
        val isLoading: Boolean = false
    )

    sealed interface Intent {
        data class SelectDate(val date: LocalDate) : Intent
        data class InsertCard(val card: RoutineCard) : Intent
        data class DeleteCardById(val cardId: Long) : Intent
        data class UpsertDailyLog(val log: RoutineDailyLog) : Intent
    }
}
