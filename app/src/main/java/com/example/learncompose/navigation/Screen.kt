package com.example.learncompose.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Welcome : Screen
    @Serializable
    data class Login(val itemId: String = "") : Screen
}