package com.example.learncompose.feature.experiment.main

import com.example.learncompose.feature.experiment.main.navigation.ExperimentNavKey

class ExperimentContract {
    data class State(val data: String = "")
    sealed interface Intent {
        data object Logout : Intent
        data class ChangeCurrentKey(val key: ExperimentNavKey) : Intent
    }
}