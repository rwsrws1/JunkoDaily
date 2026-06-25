package com.example.learncompose.features.home.presentation

class HomeContract {
    data class State(val items: List<String> = emptyList(), var testNumber: Int = 0)
    sealed interface Intent {
        data class ClickItem(val id: String) : Intent
        object PlusItem : Intent
    }
    sealed interface SideEffect {
        data class NavigationToDetail(val id: String) : SideEffect
    }
}