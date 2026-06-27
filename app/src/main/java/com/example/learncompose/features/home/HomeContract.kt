package com.example.learncompose.features.home

class HomeContract {
    data class State(val data: String = "")
    sealed interface Intent {
        object Logout : Intent
    }
    sealed interface SideEffect {
        data class NavigateToLogin(val id: String = "") : SideEffect
    }
}