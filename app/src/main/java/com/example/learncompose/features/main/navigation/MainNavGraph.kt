package com.example.learncompose.features.main.navigation

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
import com.example.learncompose.features.home.HomeScreen
import com.example.learncompose.screen.ComponentsScreen
import com.example.learncompose.screen.DrawingBoardScreen
import com.example.learncompose.screen.MediaPickerScreen
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
            is MainNavKey.Home -> NavEntry(key) {
                HomeScreen(
                    onNavigateToLoading = onNavigateToLoading
                )
            }
            is MainNavKey.Components -> NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(animationSpec = tween(500)) { it } togetherWith ExitTransition.None
                }
            ) {
                ComponentsScreen(
                    onNavigatorToDrawingBoard = { rememberNavBackStack.add(MainNavKey.DrawingBoard) }
                )
            }
            is MainNavKey.Profile -> NavEntry(key) {
                MediaPickerScreen()
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
            else -> NavEntry(key) {}
        }
    }

}