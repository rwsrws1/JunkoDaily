package com.example.learncompose.features.welcome

class WelcomeContract {
    data class State(val items: List<String> = emptyList(), var testNumber: Int = 0)
    sealed interface Intent {
        data class ClickLogin(val id: String) : Intent
        object PlusItem : Intent
    }
    sealed interface SideEffect {
        data class NavigateToLogin(val id: String) : SideEffect
    }
}