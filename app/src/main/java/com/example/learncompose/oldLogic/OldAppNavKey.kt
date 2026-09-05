package com.example.learncompose.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface OldAppNavKey : NavKey {
    @Serializable
    data object Welcome : OldAppNavKey
    @Serializable
    data class Login(val itemId: String = "") : OldAppNavKey
    @Serializable
    data object Loading : OldAppNavKey
    @Serializable
    data object Main : OldAppNavKey
}