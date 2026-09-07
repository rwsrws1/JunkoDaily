package com.example.learncompose.feature.routine

import com.example.learncompose.core.model.RoutineCard

class RoutineContract {
    data class UiState(
        val cardList: List<RoutineCard> = listOf(),
        val isReady: Boolean = false
    )

    sealed interface Intent {
        sealed interface ViewModelIntent : Intent
        data class InsertRoutineCard(val routineCard: RoutineCard) : ViewModelIntent
        data class DeleteRoutineCard(val routineCard: RoutineCard) : ViewModelIntent
        data class UpdateRoutineCard(val routineCard: RoutineCard) : ViewModelIntent
        data class ShowMessage(val message: String = "") : Intent
    }
    sealed interface SideEffect {
        data class LoginAsVisitor(val id: String = "") : SideEffect
    }
}
