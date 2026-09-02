package com.example.learncompose.feature.experiment.features.main

import com.example.learncompose.feature.experiment.features.main.navigation.MainNavKey

class MainContract {
    data class State(val data: String = "")
    sealed interface Intent {
        data object Logout : Intent
        data class ChangeCurrentKey(val key: MainNavKey) : Intent
    }
}