package com.example.learncompose.feature.experiment.features.home.navigation

import androidx.navigation3.runtime.NavKey

sealed interface HomeNavKey : NavKey {
    data object Travel : HomeNavKey
    data object Chart : HomeNavKey
    data object MainIcon : HomeNavKey
    data object Component : HomeNavKey
}