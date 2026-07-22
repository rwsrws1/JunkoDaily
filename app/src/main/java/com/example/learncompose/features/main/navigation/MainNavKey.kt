package com.example.learncompose.features.main.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MainNavKey : NavKey {
    @Serializable
    data object Components : MainNavKey
    @Serializable
    data object Home : MainNavKey
    @Serializable
    data object Profile : MainNavKey
    @Serializable
    data object DrawingBoard : MainNavKey
}