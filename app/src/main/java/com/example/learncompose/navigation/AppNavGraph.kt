package com.example.learncompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
    val entryProvider: (Screen) -> NavEntry<Screen> = { key: Screen ->
        when (key) {
            is Screen.Welcome -> NavEntry(key) {
                val viewModel = remember { WelcomeViewModel() }
                WelcomeScreen(
                    onNavigateToLogin = { id ->
                        backStack.add(Screen.Login(id))
                    },
                    viewModel = viewModel
                )
            }

            is Screen.Login -> NavEntry(key) {
                LoginScreen()
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider
    )
}