package com.example.learncompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.learncompose.features.login.presentation.LoginScreen
import com.example.learncompose.features.welcome.presentation.WelcomeScreen
import com.example.learncompose.features.welcome.presentation.WelcomeViewModel

@Composable
fun AppNavGraph() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Welcome) }
    val rememberNavBackStack = rememberNavBackStack(Screen.Welcome)

    val entryProvider: (NavKey) -> NavEntry<NavKey> = { key: NavKey ->
        when (key) {
            is Screen.Welcome -> NavEntry(key) {
                val viewModel = viewModel<WelcomeViewModel>()
                WelcomeScreen(
                    onNavigateToLogin = { id ->
                        rememberNavBackStack.add(Screen.Login(id))
                    },
                    viewModel = viewModel
                )
            }

            is Screen.Login -> NavEntry(key) {
                LoginScreen()
            }

            else -> NavEntry(key) {}
        }
    }

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = rememberNavBackStack,
        onBack = { rememberNavBackStack.removeLastOrNull() },
        entryProvider = entryProvider
    )
}