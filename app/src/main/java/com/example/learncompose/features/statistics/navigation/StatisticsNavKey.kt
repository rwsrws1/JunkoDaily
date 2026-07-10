package com.example.learncompose.features.statistics.navigation

import androidx.navigation3.runtime.NavKey

sealed interface StatisticsNavKey : NavKey {
    data object Year : StatisticsNavKey
    data object Month : StatisticsNavKey
    data object Day : StatisticsNavKey
}