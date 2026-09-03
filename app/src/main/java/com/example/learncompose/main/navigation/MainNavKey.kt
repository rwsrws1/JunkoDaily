package com.example.learncompose.main.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MainNavKey : NavKey {
    @Serializable
    data object Experiment : MainNavKey
    @Serializable
    data object DrawingBoard : MainNavKey
    @Serializable
    data object ScratchCard : MainNavKey
    @Serializable
    data object Note : MainNavKey
    @Serializable
    data object Habit : MainNavKey
    @Serializable
    data object Spend : MainNavKey
}