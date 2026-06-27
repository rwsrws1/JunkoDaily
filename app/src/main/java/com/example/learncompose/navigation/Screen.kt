package com.example.learncompose.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable
    data object Welcome : Screen
    @Serializable
    data class Login(val itemId: String = "") : Screen
    @Serializable
    data object Loading : Screen
    @Serializable
    data object Home : Screen
}