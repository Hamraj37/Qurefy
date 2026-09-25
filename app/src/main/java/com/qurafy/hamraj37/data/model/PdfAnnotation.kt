package com.qurafy.hamraj37.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PdfPathPoint(
    val xRatio: Float,
    val yRatio: Float
)

@Serializable
data class PdfDrawingStroke(
    val points: List<PdfPathPoint>,
    val colorArgb: Long,
    val strokeWidth: Float,
    val isHighlighter: Boolean
)
