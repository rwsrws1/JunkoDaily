package com.example.learncompose.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.learncompose.core.navigation.Navigator
import com.example.learncompose.feature.note.NoteDetailScreen
import com.example.learncompose.feature.note.NoteScreen
import com.example.learncompose.feature.routine.RoutineScreen
import com.example.learncompose.feature.spend.SpendScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.noteEntry(navigator: Navigator) {
    entry<NoteKey>(
        metadata = ListDetailSceneStrategy.listPane {
            RoutineScreen()
        }
    ) {
        NoteScreen(
            onClick = {
                navigator.navigate(NoteDetailKey)
            }
        )
    }
}

fun EntryProviderScope<NavKey>.routineEntry(navigator: Navigator) {
    entry<RoutineKey> {
        RoutineScreen(
        )
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