package com.qurafy.hamraj37

import com.qurafy.hamraj37.data.model.PdfDrawingStroke
import com.qurafy.hamraj37.data.model.PdfPathPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranAnnotationTest {

    @Test
    fun testPdfDrawingStrokeCreation() {
        val points = listOf(
            PdfPathPoint(0.1f, 0.2f),
            PdfPathPoint(0.3f, 0.4f)
        )
        val stroke = PdfDrawingStroke(
            points = points,
            colorArgb = 0xFFFFD700,
            strokeWidth = 24f,
            isHighlighter = true
        )

        assertEquals(2, stroke.points.size)
        assertEquals(0.1f, stroke.points[0].xRatio)
        assertEquals(0.2f, stroke.points[0].yRatio)
        assertTrue(stroke.isHighlighter)
        assertEquals(24f, stroke.strokeWidth)
    }
}
