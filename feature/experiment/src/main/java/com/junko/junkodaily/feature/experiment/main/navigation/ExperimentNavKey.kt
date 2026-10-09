package com.junko.junkodaily.feature.experiment.main.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface ExperimentNavKey : NavKey {
    @Serializable
    data object Experiment : ExperimentNavKey
    @Serializable
    data object DrawingBoard : ExperimentNavKey
    @Serializable
    data object ScratchCard : ExperimentNavKey
    @Serializable
    data object Note : ExperimentNavKey
    @Serializable
    data object Habit : ExperimentNavKey
    @Serializable
    data object Spend : ExperimentNavKey
}