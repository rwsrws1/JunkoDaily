package com.example.learncompose.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.learncompose.core.navigation.Navigator
import com.example.learncompose.feature.experiment.main.ExperimentScreen
import com.example.learncompose.feature.note.NoteDetailScreen
import com.example.learncompose.feature.note.NoteScreen
import com.example.learncompose.feature.routine.chart.RoutineChartScreen
import com.example.learncompose.feature.routine.RoutineViewModelScreen
import com.example.learncompose.feature.routine.chart.RoutineChartViewModelScreen
import com.example.learncompose.feature.spend.SpendScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.noteEntry(navigator: Navigator) {
    entry<NoteKey>(
        metadata = ListDetailSceneStrategy.listPane {
            RoutineViewModelScreen(viewModel = hiltViewModel(), onChartClick = {
                navigator.navigate(RoutineChartKey)
            })
        }
    ) {
        NoteScreen(
            onClick = {
                navigator.navigate(NoteDetailKey)
            },
            toExperiment = {
                navigator.navigate(ExperimentKey)
            }
        )
    }
}

fun EntryProviderScope<NavKey>.routineEntry(navigator: Navigator) {
    entry<RoutineKey> {
        RoutineViewModelScreen(viewModel = hiltViewModel(), onChartClick = {
            navigator.navigate(RoutineChartKey)
        })
    }
}

fun EntryProviderScope<NavKey>.spendEntry(navigator: Navigator) {
    entry<SpendKey> {
        SpendScreen(
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.noteDetailEntry(navigator: Navigator) {
    entry<NoteDetailKey>(
        metadata = ListDetailSceneStrategy.detailPane()
    ) {
        NoteDetailScreen(
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

fun EntryProviderScope<NavKey>.routineChartEntry(navigator: Navigator) {
    entry<RoutineChartKey>(
//        metadata = NavDisplay.transitionSpec {
//            slideInHorizontally(animationSpec = tween(500)) { it } togetherWith ExitTransition.None
//        } + NavDisplay.popTransitionSpec {
//            EnterTransition.None togetherWith slideOutHorizontally(animationSpec = tween(500)) { it }
//        }
    ) {
        RoutineChartViewModelScreen(
            viewModel = hiltViewModel(),
            onNavigationClick = {
                navigator.goBack()
            }
        )
    }
}