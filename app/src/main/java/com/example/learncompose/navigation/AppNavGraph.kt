package com.example.learncompose.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.learncompose.app.MainViewModel
import com.example.learncompose.common.CommonScreen
import com.example.learncompose.data.repository.AuthState
import com.example.learncompose.features.home.HomeScreen
import com.example.learncompose.features.login.LoginScreen
import com.example.learncompose.features.login.LoginViewModel
import com.example.learncompose.features.welcome.WelcomeScreen
import com.example.learncompose.features.welcome.WelcomeViewModel

@Composable
fun AppNavGraph(startDestination: Screen = Screen.Welcome) {
//    val backStack = remember { mutableStateListOf<Screen>(Screen.Welcome) }
    val rememberNavBackStack = rememberNavBackStack(startDestination)

    val viewModel = hiltViewModel<MainViewModel>()
    if (rememberNavBackStack.last() != Screen.Welcome && viewModel.authState == AuthState.LoggedOut) {
        rememberNavBackStack.clear()
        rememberNavBackStack.add(Screen.Login(""))
    }

    val entryProvider: (NavKey) -> NavEntry<NavKey> = { key: NavKey ->
        when (key) {
            is Screen.Welcome -> NavEntry(key) {
                val viewModel = hiltViewModel<WelcomeViewModel>()
                WelcomeScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { id ->
                        rememberNavBackStack.add(Screen.Login(""))
                    }
                )
            }

            is Screen.Login -> NavEntry(key) {
                val viewModel = hiltViewModel<LoginViewModel>()
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToHome = {
                        rememberNavBackStack.add(Screen.Home)
                    }
                )
            }

            is Screen.Home -> NavEntry(key) {
                HomeScreen()
                rememberNavBackStack.remove(Screen.Login(""))
                rememberNavBackStack.remove(Screen.Welcome)
            }

            is Screen.Loading -> NavEntry(key) {
                CommonScreen()
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