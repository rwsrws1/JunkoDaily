package com.example.learncompose.feature.routine

import com.example.learncompose.core.model.RoutineCard
import java.time.LocalDate

class RoutineContract {
    data class UiState(
        val cardList: List<RoutineCard> = listOf(),
        val currentRecordDate: LocalDate = LocalDate.now(),
        val isReady: Boolean = false
    )

    sealed interface Intent {
        data class InsertRoutineCard(val routineCard: RoutineCard) : Intent
        data class DeleteRoutineCard(val routineCard: RoutineCard) : Intent
        data class UpdateRoutineCard(val routineCard: RoutineCard) : Intent
    }
    sealed interface SideEffect {
        data class LoginAsVisitor(val id: String = "") : SideEffect
    }
}
