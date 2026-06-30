package com.example.learncompose.app

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.learncompose.R
import com.example.learncompose.navigation.AppNavGraph

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val startDestination = mainViewModel.startDestination

    if (startDestination == null) {
        StartScreen()
    } else {
        AppNavGraph(startDestination = startDestination)
    }
}

@Composable
fun StartScreen(modifier: Modifier = Modifier) {
    val localWindowInfoWidth = LocalWindowInfo.current.containerSize.width.dp

    Box(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
        .padding(20.dp)
    ) {
        val imageSize = when {
            localWindowInfoWidth < 600.dp -> 200
            localWindowInfoWidth < 840.dp -> 200
            else -> 400
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))
            Image(
                modifier = modifier.size(imageSize.dp),
                painter = painterResource(R.drawable.forum_24px),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.weight(1f))
        }
    }
}

@PreviewScreenSizes
@Composable
private fun Preview() {
    StartScreen()
}