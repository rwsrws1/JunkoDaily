package com.example.learncompose.core.designsystem.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.learncompose.core.designsystem.R

object AppIcons {
    val main: Painter
        @Composable
        get() = painterResource(R.drawable.menu_book_24px)
}