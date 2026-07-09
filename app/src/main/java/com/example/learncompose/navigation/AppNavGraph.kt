package com.example.learncompose.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.learncompose.app.MainViewModel
import com.example.learncompose.ui.components.CommonScreen
import com.example.learncompose.data.repository.AuthState
import com.example.learncompose.features.home.HomeScreen
import com.example.learncompose.features.home.HomeViewModel
import com.example.learncompose.features.login.LoginScreen
import com.example.learncompose.features.login.LoginViewModel
import com.example.learncompose.features.welcome.WelcomeScreen
import com.example.learncompose.features.welcome.WelcomeViewModel

@Composable
fun AppNavGraph(startDestination: AppNavKey = AppNavKey.Welcome) {
    val rememberNavBackStack = rememberNavBackStack(startDestination)

    val viewModel = hiltViewModel<MainViewModel>()
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
                    onNavigateToHome = { id ->
                        rememberNavBackStack.clear()
                        rememberNavBackStack.add(AppNavKey.Home)
                    }
                )
            }

            is AppNavKey.Login -> NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(animationSpec = tween(durationMillis = 1000)) { -it } togetherWith slideOutHorizontally(animationSpec = tween(durationMillis = 1000)) { it }
                } + NavDisplay.popTransitionSpec {
                    slideInHorizontally(animationSpec = tween(durationMillis = 1000)) { it } togetherWith slideOutHorizontally(animationSpec = tween(durationMillis = 1000)) { it }
                }
            ) {
                val viewModel = hiltViewModel<LoginViewModel>()
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToHome = {
                        rememberNavBackStack.clear()
                        rememberNavBackStack.add(AppNavKey.Home)
                    }
                )
            }

            is AppNavKey.Home -> NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(animationSpec = tween(durationMillis = 1000)) { it } togetherWith slideOutHorizontally(animationSpec = tween(durationMillis = 1000)) { -it }
                } + NavDisplay.popTransitionSpec {
                    slideInHorizontally(animationSpec = tween(durationMillis = 1000)) { -it } togetherWith slideOutHorizontally(animationSpec = tween(durationMillis = 1000)) { it }
                }
            ) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                )
            }

            is AppNavKey.Loading -> NavEntry(key) {
                CommonScreen()
            }

            else -> NavEntry(key) {}
        }
    }

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