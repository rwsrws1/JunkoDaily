package com.example.learncompose.main

import com.example.learncompose.main.navigation.MainNavKey

class MainContract {
    data class State(val data: String = "")
    sealed interface Intent {
        data object Logout : Intent
        data class ChangeCurrentKey(val key: MainNavKey) : Intent
    }
}