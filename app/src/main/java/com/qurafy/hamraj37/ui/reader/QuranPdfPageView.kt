package com.qurafy.hamraj37.ui.reader

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.qurafy.hamraj37.data.model.PdfDrawingStroke
import com.qurafy.hamraj37.data.model.PdfPathPoint

@Composable
fun QuranPdfPageView(
    pageIndex: Int,
    isNightMode: Boolean,
    renderPageBitmap: suspend (pageIndex: Int, width: Int, height: Int, isNight: Boolean) -> Bitmap?,
    modifier: Modifier = Modifier,
    isDrawingMode: Boolean = false,
    activeColor: Color = Color(0xFFFFD700),
    isHighlighter: Boolean = true,
    savedStrokes: List<PdfDrawingStroke> = emptyList(),
    onAddStroke: (PdfDrawingStroke) -> Unit = {},
    onToggleControls: () -> Unit = {}
) {
    var pageBitmap by remember(pageIndex, isNightMode) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(pageIndex, isNightMode) { mutableStateOf(true) }

    // Zoom & Pan gesture states
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Live drawing state
    val livePoints = remember { mutableStateListOf<PdfPathPoint>() }

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (isNightMode) MaterialTheme.colorScheme.background
                else MaterialTheme.colorScheme.surface
            ),
        contentAlignment = Alignment.Center
    ) {
        val widthPx = with(density) { constraints.maxWidth }
        val heightPx = with(density) { constraints.maxHeight }

        LaunchedEffect(pageIndex, isNightMode, widthPx, heightPx) {
            isLoading = true
            val bitmap = renderPageBitmap(pageIndex, widthPx, heightPx, isNightMode)
            pageBitmap = bitmap
            isLoading = false
        }

        if (isLoading || pageBitmap == null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Loading Quran Page $pageIndex...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            pageBitmap?.let { bmp ->
                val transformModifier = if (!isDrawingMode) {
                    Modifier.pointerInput(pageIndex, scale) {
                        awaitEachGesture {
                            var zoomAccumulator = 1f
                            var panAccumulator = Offset.Zero
                            var pastSlop = false
                            val touchSlop = viewConfiguration.touchSlop

                            do {
                                val event = awaitPointerEvent()
                                val canceled = event.changes.any { it.isConsumed }
                                if (!canceled) {
                                    val pointerCount = event.changes.size
                                    if (pointerCount > 1 || scale > 1f) {
                                        val zoomChange = event.calculateZoom()
                                        val panChange = event.calculatePan()

                                        if (!pastSlop) {
                                            zoomAccumulator *= zoomChange
                                            panAccumulator += panChange
                                            val centroidSize = event.calculateCentroidSize()
                                            val zoomMotion = kotlin.math.abs(1 - zoomAccumulator) * centroidSize
                                            val panMotion = panAccumulator.getDistance()

                                            if (zoomMotion > touchSlop || panMotion > touchSlop) {
                                                pastSlop = true
                                            }
                                        }

                                        if (pastSlop) {
                                            if (zoomChange != 1f || panChange != Offset.Zero) {
                                                val newScale = (scale * zoomChange).coerceIn(1f, 3.5f)
                                                if (newScale > 1f) {
                                                    scale = newScale
                                                    val maxOffsetX = (constraints.maxWidth * (scale - 1f)) / 2f
                                                    val maxOffsetY = (constraints.maxHeight * (scale - 1f)) / 2f
                                                    val newX = (offset.x + panChange.x).coerceIn(-maxOffsetX, maxOffsetX)
                                                    val newY = (offset.y + panChange.y).coerceIn(-maxOffsetY, maxOffsetY)
                                                    offset = Offset(newX, newY)
                                                } else {
                                                    scale = 1f
                                                    offset = Offset.Zero
                                                }
                                                event.changes.forEach { it.consume() }
                                            }
                                        }
                                    }
                                }
                            } while (event.changes.any { it.pressed })
                        }
                    }
                } else {
                    Modifier
                }

                val drawingModifier = if (isDrawingMode) {
                    Modifier.pointerInput(pageIndex, activeColor, isHighlighter, scale, offset) {
                        fun convertTouchToRatio(touchOffset: Offset): PdfPathPoint {
                            val w = constraints.maxWidth.toFloat()
                            val h = constraints.maxHeight.toFloat()
                            val px = (touchOffset.x - w / 2f - offset.x) / scale + w / 2f
                            val py = (touchOffset.y - h / 2f - offset.y) / scale + h / 2f
                            return PdfPathPoint(
                                xRatio = (px / w).coerceIn(0f, 1f),
                                yRatio = (py / h).coerceIn(0f, 1f)
                            )
                        }

                        detectDragGestures(
                            onDragStart = { startOffset ->
                                livePoints.clear()
                                livePoints.add(convertTouchToRatio(startOffset))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                livePoints.add(convertTouchToRatio(change.position))
                            },
                            onDragEnd = {
                                if (livePoints.size >= 2) {
                                    val newStroke = PdfDrawingStroke(
                                        points = livePoints.toList(),
                                        colorArgb = activeColor.toArgb().toLong(),
                                        strokeWidth = if (isHighlighter) 24f else 6f,
                                        isHighlighter = isHighlighter
                                    )
                                    onAddStroke(newStroke)
                                }
                                livePoints.clear()
                            },
                            onDragCancel = {
                                livePoints.clear()
                            }
                        )
                    }
                } else {
                    Modifier
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(transformModifier)
                        .then(drawingModifier)
                        .pointerInput(pageIndex, isDrawingMode) {
                            if (!isDrawingMode) {
                                detectTapGestures(
                                    onTap = { onToggleControls() },
                                    onDoubleTap = {
                                        if (scale > 1f) {
                                            scale = 1f
                                            offset = Offset.Zero
                                        } else {
                                            scale = 2f
                                        }
                                    }
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // 1. PDF Page Image
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Quran Page $pageIndex",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )

                    // 2. Annotation Canvas Overlay
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        fun drawSingleStroke(stroke: PdfDrawingStroke) {
                            if (stroke.points.size < 2) return
                            val path = Path()
                            stroke.points.forEachIndexed { index, pt ->
                                val px = pt.xRatio * canvasWidth
                                val py = pt.yRatio * canvasHeight
                                if (index == 0) {
                                    path.moveTo(px, py)
                                } else {
                                    path.lineTo(px, py)
                                }
                            }
                            val drawColor = Color(stroke.colorArgb)
                            val alpha = if (stroke.isHighlighter) 0.45f else 1.0f
                            drawPath(
                                path = path,
                                color = drawColor.copy(alpha = alpha),
                                style = Stroke(
                                    width = stroke.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        // Draw saved strokes
                        savedStrokes.forEach { stroke ->
                            drawSingleStroke(stroke)
                        }

                        // Draw live active stroke
                        if (livePoints.size >= 2) {
                            val activeStroke = PdfDrawingStroke(
                                points = livePoints.toList(),
                                colorArgb = activeColor.toArgb().toLong(),
                                strokeWidth = if (isHighlighter) 24f else 6f,
                                isHighlighter = isHighlighter
                            )
                            drawSingleStroke(activeStroke)
                        }
                    }
                }
            }
        }
    }
}
