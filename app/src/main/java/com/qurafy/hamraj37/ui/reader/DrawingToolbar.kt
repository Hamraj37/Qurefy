package com.qurafy.hamraj37.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Highlight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BrushColorOption(
    val color: Color,
    val isHighlighter: Boolean,
    val name: String
)

val defaultBrushColors = listOf(
    BrushColorOption(Color(0xFFFFD700), isHighlighter = true, name = "Highlighter"),
    BrushColorOption(Color(0xFFFF3B30), isHighlighter = false, name = "Red"),
    BrushColorOption(Color(0xFF34C759), isHighlighter = false, name = "Green"),
    BrushColorOption(Color(0xFF007AFF), isHighlighter = false, name = "Blue"),
    BrushColorOption(Color(0xFF1C1C1E), isHighlighter = false, name = "Black")
)

@Composable
fun DrawingToolbar(
    activeColor: Color,
    isHighlighter: Boolean,
    onSelectBrush: (Color, Boolean) -> Unit,
    onUndo: () -> Unit,
    onClearPage: () -> Unit,
    onCloseDrawingMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassBorderBrush = androidx.compose.runtime.remember {
        androidx.compose.ui.graphics.Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.70f),
                Color.White.copy(alpha = 0.20f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    }

    Surface(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
            .border(1.2.dp, glassBorderBrush, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 12.dp,
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Brush Colors
                defaultBrushColors.forEach { option ->
                    val isSelected = option.color == activeColor && option.isHighlighter == isHighlighter
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(option.color)
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                } else Modifier
                            )
                            .clickable { onSelectBrush(option.color, option.isHighlighter) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (option.isHighlighter) {
                            Icon(
                                imageVector = Icons.Rounded.Highlight,
                                contentDescription = "Highlighter",
                                tint = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onUndo, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Undo,
                        contentDescription = "Undo Line",
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onClearPage, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Clear Page Annotations",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Surface(
                    onClick = onCloseDrawingMode,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Done/Exit Drawing Mode",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
