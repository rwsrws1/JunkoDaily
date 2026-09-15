package com.example.learncompose.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.learncompose.core.navigation.Navigator
import com.example.learncompose.feature.experiment.main.ExperimentScreen
import com.example.learncompose.feature.routine.RoutineViewModelScreen
import com.example.learncompose.feature.chart.ChartViewModelScreen
import com.example.learncompose.feature.spend.SpendDetailScreen
import com.example.learncompose.feature.spend.SpendScreen

fun EntryProviderScope<NavKey>.chartEntry(navigator: Navigator) {
    entry<ChartKey>(
    ) {
        ChartViewModelScreen(viewModel = hiltViewModel(), naviToRoutineScreen = {
            navigator.navigate(RoutineKey)
        })
    }
}

fun EntryProviderScope<NavKey>.routineEntry(navigator: Navigator) {
    entry<RoutineKey> {
        RoutineViewModelScreen(viewModel = hiltViewModel(), naviToChartScreen = {
            navigator.navigate(ChartKey)
        })
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.spendEntry(navigator: Navigator) {
    entry<SpendKey>(
        metadata = ListDetailSceneStrategy.listPane() {
            Box(Modifier.fillMaxSize().background(Color.Black))
        }
    ) {
        SpendScreen(
            onClick = {
                navigator.navigate(SpendDetailKey)
            },
            toExperiment = {
                navigator.navigate(ExperimentKey)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.spendDetailEntry(navigator: Navigator) {
    entry<SpendDetailKey>(
        metadata = ListDetailSceneStrategy.detailPane()
    ) {
        SpendDetailScreen(
            onBack = {
                navigator.goBack()
            }
        )
    }
}

fun EntryProviderScope<NavKey>.experimentEntry(navigator: Navigator) {
    entry<ExperimentKey>(
    ) {
        ExperimentScreen(
        )
    }
}