package com.example.learncompose.features.home.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.learncompose.features.statistics.StatisticsScreen
import com.example.learncompose.ui.components.GreetingScreen
import com.example.learncompose.ui.screen.MediaPickerScreen
import kotlin.collections.listOf

@Composable
fun HomeNavGraph(rememberNavBackStack: NavBackStack<NavKey>) {

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
            is HomeNavKey.Greeting -> NavEntry(key) {
                GreetingScreen()
            }
            is HomeNavKey.Statistics -> NavEntry(key) {
                StatisticsScreen()
            }
            is HomeNavKey.Profile -> NavEntry(key) {
                MediaPickerScreen()
            }
            else -> NavEntry(key) {}
        }
    }

}