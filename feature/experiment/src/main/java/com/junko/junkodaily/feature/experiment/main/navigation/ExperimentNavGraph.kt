package com.junko.junkodaily.feature.experiment.main.navigation

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
import com.junko.junkodaily.feature.experiment.features.home.HomeScreen
import com.junko.junkodaily.feature.experiment.screen.DrawingBoardScreen
import com.junko.junkodaily.feature.experiment.screen.ScratchCardScreen
import kotlin.collections.listOf

@Composable
fun ExperimentNavGraph(
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
            is ExperimentNavKey.Experiment -> NavEntry(key) {
                HomeScreen(
                    onNavigateToLoading = onNavigateToLoading,
                    onNavigatorToDrawingBoard = { rememberNavBackStack.add(ExperimentNavKey.DrawingBoard) },
                    onNavigatorToScratchCard = { rememberNavBackStack.add(ExperimentNavKey.ScratchCard) }
                )
            }
            is ExperimentNavKey.Note -> NavEntry(key) {
            }
            is ExperimentNavKey.Habit -> NavEntry(key) {
            }
            is ExperimentNavKey.Spend -> NavEntry(key) {
            }
            is ExperimentNavKey.DrawingBoard -> NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(animationSpec = tween(500)) { it } togetherWith ExitTransition.None
                } + NavDisplay.popTransitionSpec {
                    EnterTransition.None togetherWith slideOutHorizontally(animationSpec = tween(500)) { it }
                }
            ) {
                DrawingBoardScreen()
            }
            is ExperimentNavKey.ScratchCard -> NavEntry(key) {
                ScratchCardScreen()
            }
            else -> NavEntry(key) {}
        }
    }

}