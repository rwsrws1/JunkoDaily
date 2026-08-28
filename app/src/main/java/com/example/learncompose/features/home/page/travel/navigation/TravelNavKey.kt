package com.example.learncompose.features.home.page.travel.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface TravelNavKey : NavKey {
    @Serializable
    data object TravelList : TravelNavKey
    @Serializable
    data object TravelDetail : TravelNavKey
}