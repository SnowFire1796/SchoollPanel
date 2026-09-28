package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = NavyPrimaryDark,
    onPrimary = SlateBackgroundDark,
    primaryContainer = NavyPrimary,
    onPrimaryContainer = NavyPrimaryDark,
    secondary = SapphireSecondaryDark,
    secondaryContainer = SapphireSecondary,
    onSecondaryContainer = SapphireSecondaryDark,
    tertiary = EmeraldTertiaryDark,
    background = SlateBackgroundDark,
    surface = SlateSurfaceDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onBackground = SlateTextPrimaryDark,
    onSurface = SlateTextPrimaryDark,
    onSurfaceVariant = SlateTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = NavyOnPrimary,
    primaryContainer = NavyPrimaryContainer,
    onPrimaryContainer = NavyOnPrimaryContainer,
    secondary = SapphireSecondary,
    onSecondary = NavyOnPrimary,
    secondaryContainer = SapphireSecondaryContainer,
    onSecondaryContainer = SapphireOnSecondaryContainer,
    tertiary = EmeraldTertiary,
    onTertiary = NavyOnPrimary,
    tertiaryContainer = EmeraldTertiaryContainer,
    onTertiaryContainer = EmeraldOnTertiaryContainer,
    background = SlateBackground,
    surface = SlateSurface,
    surfaceVariant = SlateSurfaceVariant,
    onBackground = SlateTextPrimary,
    onSurface = SlateTextPrimary,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateOutline
)

@Composable
fun SchoolPanelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
