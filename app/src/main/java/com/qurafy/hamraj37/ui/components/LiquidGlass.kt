package com.qurafy.hamraj37.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun isAppInDarkTheme(): Boolean {
    val surfaceColor = MaterialTheme.colorScheme.surface
    return remember(surfaceColor) { surfaceColor.luminance() < 0.5f }
}

@Composable
fun rememberGlassBorderBrush(isDark: Boolean = isAppInDarkTheme()): Brush {
    return remember(isDark) {
        if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.60f),
                    Color.White.copy(alpha = 0.20f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.90f),
                    Color.White.copy(alpha = 0.40f),
                    Color.White.copy(alpha = 0.15f)
                )
            )
        }
    }
}

@Composable
fun rememberGlassBgColor(isDark: Boolean = isAppInDarkTheme()): Color {
    return remember(isDark) {
        if (isDark) Color(0xCC1E1E1E) else Color(0xF2FFFFFF)
    }
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    elevation: Dp = 2.dp,
    borderWidth: Dp = 1.2.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val glassBorderBrush = rememberGlassBorderBrush()

    Card(
        modifier = modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        border = BorderStroke(borderWidth, glassBorderBrush),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}
