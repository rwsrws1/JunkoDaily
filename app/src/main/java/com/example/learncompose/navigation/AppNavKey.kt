package com.example.learncompose.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppNavKey : NavKey {
    @Serializable
    data object Welcome : AppNavKey
    @Serializable
    data class Login(val itemId: String = "") : AppNavKey
    @Serializable
    data object Loading : AppNavKey
    @Serializable
    data object Home : AppNavKey
}