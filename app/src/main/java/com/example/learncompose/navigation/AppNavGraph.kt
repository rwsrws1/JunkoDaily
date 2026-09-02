package com.example.learncompose.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.learncompose.app.AppViewModel
import com.example.learncompose.feature.experiment.data.repo.AuthState
import com.example.learncompose.feature.experiment.features.login.LoginScreen
import com.example.learncompose.feature.experiment.features.login.LoginViewModel
import com.example.learncompose.feature.experiment.features.main.MainScreen
import com.example.learncompose.feature.experiment.features.main.MainViewModel
import com.example.learncompose.feature.experiment.features.welcome.WelcomeScreen
import com.example.learncompose.feature.experiment.features.welcome.WelcomeViewModel
import com.example.learncompose.app.MainIconScreen

val LocalAppNavigator = staticCompositionLocalOf<(AppNavKey) -> Unit> {
    {}
}

@Composable
fun AppNavGraph(startDestination: AppNavKey = AppNavKey.Welcome) {
    val rememberNavBackStack = rememberNavBackStack(startDestination)

    val viewModel = hiltViewModel<AppViewModel>()
    // 全局登录状态拦截
    if (rememberNavBackStack.last() != AppNavKey.Welcome && viewModel.authState == AuthState.LoggedOut) {
        rememberNavBackStack.clear()
        rememberNavBackStack.add(AppNavKey.Login(""))
    }

    val entryProvider: (NavKey) -> NavEntry<NavKey> = { key: NavKey ->
        when (key) {
            is AppNavKey.Welcome -> NavEntry(key) {
                val viewModel = hiltViewModel<WelcomeViewModel>()
                WelcomeScreen(
                    viewModel = viewModel,
                    onNavigateToMain = { id ->
                        rememberNavBackStack.clear()
                        rememberNavBackStack.add(AppNavKey.Main)
                    }
                )
            }

            is AppNavKey.Login -> NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec {
                    fadeIn(tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                }
            ) {
                val viewModel = hiltViewModel<LoginViewModel>()
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToMain = {
                        rememberNavBackStack.clear()
                        rememberNavBackStack.add(AppNavKey.Main)
                    }
                )
            }

            is AppNavKey.Main -> NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec {
                    scaleIn(animationSpec = tween(500)) togetherWith fadeOut(tween(500))
                }
            ) {
                val viewModel = hiltViewModel<MainViewModel>()
                MainScreen(
                    viewModel = viewModel,
                    onNavigateToLoading = {
                        rememberNavBackStack.add(AppNavKey.Loading)
                    }
                )
            }

            is AppNavKey.Loading -> NavEntry(key) {
                MainIconScreen()
            }

            else -> NavEntry(key) {}
        }
    }

    CompositionLocalProvider(
        LocalAppNavigator provides { navKey -> rememberNavBackStack.add(navKey) }
    ) {
        NavDisplay<NavKey>(
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            backStack = rememberNavBackStack,
            onBack = { rememberNavBackStack.removeLastOrNull() },
            entryProvider = entryProvider,
            transitionSpec = {
                fadeIn(tween(1000)) togetherWith fadeOut(tween(1000))
            },
            popTransitionSpec = {
                fadeIn(tween(1000)) togetherWith fadeOut(tween(1000))
            }
        )
    }
}