package com.junko.junkodaily.feature.experiment.features.welcome

import com.junko.junkodaily.feature.experiment.data.repo.AuthState

class WelcomeContract {
    data class State(
        val items: List<String> = emptyList(),
        var testNumber: Int = 0,
        val authState: AuthState = AuthState.Loading
    )
    sealed interface Intent {
        sealed interface ViewModelIntent : Intent {}
        data class ClickEnter(val id: String = "") : ViewModelIntent
        data object PlusItem : ViewModelIntent
        data class ShowMessage(val message: String = "") : Intent
    }
    sealed interface SideEffect {
        data class LoginAsVisitor(val id: String = "") : SideEffect
    }
}