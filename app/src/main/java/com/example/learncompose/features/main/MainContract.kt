package com.example.learncompose.features.main

import com.example.learncompose.features.main.navigation.MainNavKey

class MainContract {
    data class State(val data: String = "")
    sealed interface Intent {
        data object UserInfo : Intent
        data class ChangeCurrentKey(val key: MainNavKey) : Intent
    }
}