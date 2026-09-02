package com.example.learncompose.feature.experiment.features.login

import com.example.learncompose.feature.experiment.data.repo.AuthState

class LoginContract {
    data class State(
        val title: String = "个人中心",
        val authState: AuthState = AuthState.Loading,
        val isRefreshing: Boolean = false
    )

    sealed interface Intent {
        data class ClickLogin(val account: String = "", val password: String = "") : Intent
    }

    sealed interface SideEffect {
        data object NavigateToMain : SideEffect
    }
}