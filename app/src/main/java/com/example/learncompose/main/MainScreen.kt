package com.example.learncompose.main

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.learncompose.core.designsystem.icons.AppIcons
import com.example.learncompose.core.navigation.rememberNavigationState
import com.example.learncompose.navigation.NoteKey
import com.example.learncompose.navigation.TOP_LEVEL_NAV_ITEMS

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState(NoteKey, TOP_LEVEL_NAV_ITEMS.keys)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                item(
                    selected = navKey == navigationState.currentTopLevelKey,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = if (navKey == navigationState.currentTopLevelKey) {
                                ImageVector.vectorResource(navItem.selectIconId)
                            } else {
                                ImageVector.vectorResource(navItem.unSelectIconId)
                            },
                            contentDescription = ""
                        )
                    },
                    label = { Text(stringResource(navItem.iconTextId)) },
                )
            }
        },
//        layoutType = NavigationSuiteScaffoldDefaults
//            .calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    ) {

    }

}

@PreviewScreenSizes
@Composable
private fun Preview() {
    MainScreen()
}