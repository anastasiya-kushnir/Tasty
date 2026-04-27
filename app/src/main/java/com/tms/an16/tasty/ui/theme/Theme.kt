package com.tms.an16.tasty.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = BackgroundLight,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = Darker,
    onBackground = Darker,
    onSurface = Darker,
    outline = StrokeColorLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = BackgroundDark,
    surface = Dark,
    onPrimary = White,
    onSecondary = White,
    onTertiary = Darker,
    onBackground = LightMediumGrey,
    onSurface = LightMediumGrey,
    outline = StrokeColorDark
)

@Composable
fun TastyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
