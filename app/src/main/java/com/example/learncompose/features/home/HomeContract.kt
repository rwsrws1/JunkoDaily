package com.example.learncompose.features.home

import com.example.learncompose.features.home.navigation.HomeNavKey

class HomeContract {
    data class State(val data: String = "")
    sealed interface Intent {
//        object Login : Intent
        object UserInfo : Intent
        data class ChangeCurrentKey(val key: HomeNavKey) : Intent
    }
//    sealed interface SideEffect {
//        object NavigateToLogin : SideEffect
//    }
}