package com.example.learncompose.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.learncompose.core.designsystem.icons.AppIcons

@Composable
fun MainIconScreen(modifier: Modifier = Modifier) {
    CombineContent()
}

@Composable
fun CombineContent(modifier: Modifier = Modifier) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.displayCutout.only(sides = WindowInsetsSides.Horizontal))
            .padding(20.dp)
    ) {
        var imageSize = 0
        imageSize = if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            200
        } else if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            150
        } else {
            150
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Icon(
                modifier = Modifier.size(imageSize.dp),
                imageVector = ImageVector.vectorResource(AppIcons.main),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.weight(1f))
        }
    }
}

@PreviewScreenSizes
@Composable
private fun Preview() {
    CombineContent()
}