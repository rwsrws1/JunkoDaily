package com.example.learncompose.features.welcome.presentation

class WelcomeContract {
    data class State(val items: List<String> = emptyList())
    sealed interface Intent {
        data class ClickItem(val id: String) : Intent
    }
    sealed interface SideEffect {
        data class NavigateToLogin(val id: String) : SideEffect
    }
}