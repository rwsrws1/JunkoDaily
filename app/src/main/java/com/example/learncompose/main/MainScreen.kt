package com.example.learncompose.main

import androidx.compose.foundation.background
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import com.example.learncompose.core.designsystem.Container
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

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val defaultLayoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    // 2. 检查是否为手机横屏（高度紧凑 + 宽度非紧凑）
    val isPhoneLandscape = !adaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) &&
            adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    // 3. 如果是手机横屏，强行覆盖为 NavigationRail
    val customLayoutType = if (isPhoneLandscape) {
        NavigationSuiteType.NavigationRail
    } else {
        defaultLayoutType
    }

    val defaultItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(),
        navigationRailItemColors = NavigationRailItemDefaults.colors(),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
    )

//    NavigationSuiteScaffold(
//        navigationSuiteItems = {
//            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
//                item(
//                    selected = navKey == navigationState.currentTopLevelKey,
//                    onClick = {
//                        navigator.navigate(navKey)
//                    },
//                    icon = {
//                        Icon(
//                            painter = if (navKey == navigationState.currentTopLevelKey) {
//                                painterResource(navItem.selectIconId)
//                            } else {
//                                painterResource(navItem.unSelectIconId)
//                            },
//                            contentDescription = ""
//                        )
//                    },
//                    label = { Text(stringResource(navItem.iconTextId)) },
//                    colors = defaultItemColors
//                )
//            }
//        },
//        layoutType = customLayoutType,
//        navigationSuiteColors = NavigationSuiteDefaults.colors()
//    ) {
        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
        val entryProvider = entryProvider {
            chartEntry(navigator)
            routineEntry(navigator)
            spendEntry(navigator)
            spendDetailEntry(navigator)
            experimentEntry(navigator)
        }
        val entries = navigationState.toEntries(entryProvider)
        println("Current entries$entries, size: ${entries.size}")
        NavDisplay(
            modifier = Modifier.background(Container),
            entries = entries,
            sceneStrategies = listOf(
                listDetailStrategy,
                SinglePaneSceneStrategy()
            ),
            onBack = { navigator.goBack() },
        )
//    }
}

@PreviewScreenSizes
@Composable
private fun Preview() {
    MainScreen()
}