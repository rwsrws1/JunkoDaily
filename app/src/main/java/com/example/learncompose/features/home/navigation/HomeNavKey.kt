package com.example.learncompose.features.home.navigation

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface HomeNavKey : NavKey {
    @Serializable
    data object Greeting : HomeNavKey
    @Serializable
    data object Statistics : HomeNavKey
    @Serializable
    data object Profile : HomeNavKey
    @Serializable
    data object DrawingBoard : HomeNavKey
}