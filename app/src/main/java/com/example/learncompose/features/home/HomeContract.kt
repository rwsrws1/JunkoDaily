package com.example.learncompose.features.home

class HomeContract {
    data class State(val data: String = "")
    sealed interface Intent {
//        object Login : Intent
        object UserInfo : Intent
    }
//    sealed interface SideEffect {
//        object NavigateToLogin : SideEffect
//    }
}