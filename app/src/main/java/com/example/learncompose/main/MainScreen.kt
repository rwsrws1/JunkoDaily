package com.example.learncompose.main

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.example.learncompose.core.navigation.Navigator
import com.example.learncompose.core.navigation.rememberNavigationState
import com.example.learncompose.core.navigation.toEntries
import com.example.learncompose.navigation.RoutineKey
import com.example.learncompose.navigation.TOP_LEVEL_NAV_ITEMS
import com.example.learncompose.navigation.experimentEntry
import com.example.learncompose.navigation.spendDetailEntry
import com.example.learncompose.navigation.chartEntry
import com.example.learncompose.navigation.routineEntry
import com.example.learncompose.navigation.spendEntry

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState(RoutineKey, TOP_LEVEL_NAV_ITEMS.keys)
    val navigator = remember { Navigator(navigationState) }
    val isInTopLevel = navigationState.currentKey in navigationState.topLevelKeys

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                item(
                    selected = navKey == navigationState.currentTopLevelKey,
                    onClick = {
                        navigator.navigate(navKey)
                    },
                    icon = {
                        Icon(
                            painter = if (navKey == navigationState.currentTopLevelKey) {
                                painterResource(navItem.selectIconId)
                            } else {
                                painterResource(navItem.unSelectIconId)
                            },
                            contentDescription = ""
                        )
                    },
                    label = { Text(stringResource(navItem.iconTextId)) },
                )
            }
        },
        layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    ) {
        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
        val entryProvider = entryProvider {
            chartEntry(navigator)
            routineEntry(navigator)
            spendEntry(navigator)
            spendDetailEntry(navigator)
            experimentEntry(navigator)
        }
        val entries = navigationState.toEntries(entryProvider)
        println("Current entries size: ${entries.size}")
        NavDisplay(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            entries = entries,
            sceneStrategies = listOf(
                listDetailStrategy,
                SinglePaneSceneStrategy()
            ),
            onBack = { navigator.goBack() },
        )
    }
}

@PreviewScreenSizes
@Composable
private fun Preview() {
    MainScreen()
}