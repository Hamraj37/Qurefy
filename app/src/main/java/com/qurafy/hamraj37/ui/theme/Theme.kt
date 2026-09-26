package com.qurafy.hamraj37.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Qurafy Emerald Palette (Fallback for Android < 12 or when dynamicColor = false)
val PrimaryEmerald = Color(0xFF0F6B56)
val OnPrimaryEmerald = Color(0xFFFFFFFF)
val PrimaryContainerEmerald = Color(0xFFE2F3ED)
val OnPrimaryContainerEmerald = Color(0xFF022B20)

val SecondaryTeal = Color(0xFF3B635A)
val OnSecondaryTeal = Color(0xFFFFFFFF)
val SecondaryContainerTeal = Color(0xFFDFEDE8)
val OnSecondaryContainerTeal = Color(0xFF0B201A)

val TertiaryOlive = Color(0xFF55624C)
val TertiaryContainerOlive = Color(0xFFD8E7CC)
val OnTertiaryContainerOlive = Color(0xFF131E0D)

val LightBackground = Color(0xFFFAFAF7)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEEF3F0)
val LightOnSurface = Color(0xFF191C1A)
val LightOnSurfaceVariant = Color(0xFF404945)

// Dark Theme Palette
val PrimaryEmeraldDark = Color(0xFF80D5C3)
val OnPrimaryEmeraldDark = Color(0xFF00382B)
val PrimaryContainerEmeraldDark = Color(0xFF005142)
val OnPrimaryContainerEmeraldDark = Color(0xFF9CF2DF)

val DarkBackground = Color(0xFF121413)
val DarkSurface = Color(0xFF181C1B)
val DarkSurfaceVariant = Color(0xFF262D2A)
val DarkOnSurface = Color(0xFFE1E3DF)
val DarkOnSurfaceVariant = Color(0xFFC0C9C4)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryEmerald,
    onPrimary = OnPrimaryEmerald,
    primaryContainer = PrimaryContainerEmerald,
    onPrimaryContainer = OnPrimaryContainerEmerald,
    secondary = SecondaryTeal,
    onSecondary = OnSecondaryTeal,
    secondaryContainer = SecondaryContainerTeal,
    onSecondaryContainer = OnSecondaryContainerTeal,
    tertiaryContainer = TertiaryContainerOlive,
    onTertiaryContainer = OnTertiaryContainerOlive,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryEmeraldDark,
    onPrimary = OnPrimaryEmeraldDark,
    primaryContainer = PrimaryContainerEmeraldDark,
    onPrimaryContainer = OnPrimaryContainerEmeraldDark,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant
)

@Composable
fun QurafyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Enables Monet Material You dynamic color on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
