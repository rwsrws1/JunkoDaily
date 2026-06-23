package com.example.learncompose.features.home.presentation

class HomeContract {
    data class State(val items: List<String> = emptyList())
    sealed interface Intent {
        data class ClickItem(val id: String) : Intent
    }
    sealed interface SideEffect {
        data class NavigationToDetail(val id: String) : SideEffect
    }
}