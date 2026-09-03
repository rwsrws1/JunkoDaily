package com.example.learncompose.main.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.learncompose.feature.experiment.features.home.HomeScreen
import com.example.learncompose.feature.experiment.screen.ComponentsScreen
import com.example.learncompose.feature.experiment.screen.DrawingBoardScreen
import com.example.learncompose.feature.experiment.screen.MediaPickerScreen
import com.example.learncompose.feature.experiment.screen.ScratchCardScreen
import com.example.learncompose.feature.habit.HabitScreen
import com.example.learncompose.feature.note.NoteScreen
import com.example.learncompose.feature.spend.SpendScreen
import kotlin.collections.listOf

@Composable
fun MainNavGraph(
    rememberNavBackStack: NavBackStack<NavKey>,
    onNavigateToLoading: () -> Unit = {}
) {

    NavDisplay<NavKey>(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = rememberNavBackStack,
        onBack = { rememberNavBackStack.removeLastOrNull() },
        transitionSpec = {
            fadeIn(tween(100)) togetherWith fadeOut(tween(100))
        },
        popTransitionSpec = {
            fadeIn(tween(100)) togetherWith fadeOut(tween(100))
        }
    ) { key ->
        when (key) {
            is MainNavKey.Experiment -> NavEntry(key) {
                HomeScreen(
                    onNavigateToLoading = onNavigateToLoading,
                    onNavigatorToDrawingBoard = { rememberNavBackStack.add(MainNavKey.DrawingBoard) },
                    onNavigatorToScratchCard = { rememberNavBackStack.add(MainNavKey.ScratchCard) }
                )
            }
            is MainNavKey.Note -> NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(animationSpec = tween(500)) { it } togetherWith ExitTransition.None
                }
            ) {
                NoteScreen()
            }
            is MainNavKey.Habit -> NavEntry(key) {
                HabitScreen()
            }
            is MainNavKey.Spend -> NavEntry(key) {
                SpendScreen()
            }
            is MainNavKey.DrawingBoard -> NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(animationSpec = tween(500)) { it } togetherWith ExitTransition.None
                } + NavDisplay.popTransitionSpec {
                    EnterTransition.None togetherWith slideOutHorizontally(animationSpec = tween(500)) { it }
                }
            ) {
                DrawingBoardScreen()
            }
            is MainNavKey.ScratchCard -> NavEntry(key) {
                ScratchCardScreen()
            }
            else -> NavEntry(key) {}
        }
    }

}