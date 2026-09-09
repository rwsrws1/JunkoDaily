package com.example.learncompose.feature.routine

import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardWithLog
import com.example.learncompose.core.model.RoutineDailyLog
import java.time.LocalDate

class RoutineContract {
    data class UiState(
        val routineCardWithLogList: List<RoutineCardWithLog> = listOf(),
        val currentRecordDate: LocalDate = LocalDate.now(),
        val isReady: Boolean = false
    )

    sealed interface Intent {
        data class InsertCard(val card: RoutineCard) : Intent
        data class DeleteCardById(val cardId: Long) : Intent
        data class UpsertDailyLog(val log: RoutineDailyLog) : Intent
    }
    sealed interface SideEffect {
        data class LoginAsVisitor(val id: String = "") : SideEffect
    }
}
