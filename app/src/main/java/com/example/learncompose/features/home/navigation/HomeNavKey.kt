package com.example.learncompose.features.home.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface HomeNavKey : NavKey {
    @Serializable
    object Greeting : HomeNavKey
    @Serializable
    object CharDemo : HomeNavKey
    @Serializable
    object MediaPiker : HomeNavKey
}