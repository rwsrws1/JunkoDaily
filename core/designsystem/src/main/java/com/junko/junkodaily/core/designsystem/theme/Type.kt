package com.junko.junkodaily.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

import com.junko.junkodaily.core.designsystem.R


val appFontFamily = FontFamily(
    androidx.compose.ui.text.font.Font(resId = R.font.mine)
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = appFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = appFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = appFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = appFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = appFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = appFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = appFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = appFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = appFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = appFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = appFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = appFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = appFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = appFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = appFontFamily),
)

