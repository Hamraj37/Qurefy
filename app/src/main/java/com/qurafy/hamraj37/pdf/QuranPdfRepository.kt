package com.qurafy.hamraj37.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class QuranPdfRepository(private val context: Context) {

    companion object {
        // Page 1 is Cover Image, Pages 2 & 3 are Index Pages
        const val PDF_FRONT_MATTER_OFFSET = 3
    }

    private val pdfAssetName = "quran-roman-urdu-hindi.pdf"
    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null

    // LruCache storing up to 30 rendered page bitmaps
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = (maxMemory / 8).coerceAtLeast(1024 * 16) // ~16MB minimum
    private val bitmapCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    @Synchronized
    private fun ensureRendererInitialized() {
        if (pdfRenderer != null) return

        try {
            val cacheFile = File(context.cacheDir, "quran_roman_urdu_hindi_cached.pdf")
            if (!cacheFile.exists() || cacheFile.length() == 0L) {
                context.assets.open(pdfAssetName).use { input ->
                    FileOutputStream(cacheFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            fileDescriptor = ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY)
            fileDescriptor?.let { fd ->
                pdfRenderer = PdfRenderer(fd)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPageCount(): Int {
        ensureRendererInitialized()
        val totalPdfPages = pdfRenderer?.pageCount ?: (604 + PDF_FRONT_MATTER_OFFSET)
        return (totalPdfPages - PDF_FRONT_MATTER_OFFSET).coerceAtLeast(1)
    }

    suspend fun renderPage(
        quranPageNumber: Int, // 1-based Quran page index (1..604)
        targetWidth: Int,
        targetHeight: Int,
        isNightMode: Boolean
    ): Bitmap? = withContext(Dispatchers.IO) {
        ensureRendererInitialized()
        val renderer = pdfRenderer ?: return@withContext null

        val maxQuranPage = (renderer.pageCount - PDF_FRONT_MATTER_OFFSET).coerceAtLeast(1)
        val safeQuranPage = quranPageNumber.coerceIn(1, maxQuranPage)

        // Convert 1-based Quran page number to 0-based PDF page index
        // e.g. Quran Page 1 -> (1 + 3) - 1 = 3 (4th page in PDF descriptor)
        val pdfPageIndex = (safeQuranPage + PDF_FRONT_MATTER_OFFSET - 1).coerceIn(0, renderer.pageCount - 1)

        val cacheKey = "p_${safeQuranPage}_w${targetWidth}_h${targetHeight}_night$isNightMode"
        val cached = bitmapCache.get(cacheKey)
        if (cached != null && !cached.isRecycled) {
            return@withContext cached
        }

        var pageBitmap: Bitmap? = null
        synchronized(this@QuranPdfRepository) {
            try {
                renderer.openPage(pdfPageIndex).use { page ->
                    val renderWidth = if (targetWidth > 0) targetWidth else page.width * 2
                    val renderHeight = if (targetHeight > 0) targetHeight else page.height * 2

                    val bitmap = Bitmap.createBitmap(
                        renderWidth.coerceAtLeast(100),
                        renderHeight.coerceAtLeast(100),
                        Bitmap.Config.ARGB_8888
                    )

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    pageBitmap = if (isNightMode) {
                        invertBitmapForNightMode(bitmap)
                    } else {
                        bitmap
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        pageBitmap?.let { bmp ->
            bitmapCache.put(cacheKey, bmp)
        }

        pageBitmap
    }

    private fun invertBitmapForNightMode(original: Bitmap): Bitmap {
        val inverted = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(inverted)
        val paint = Paint()
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(original, 0f, 0f, paint)
        return inverted
    }

    fun close() {
        synchronized(this) {
            try {
                pdfRenderer?.close()
                pdfRenderer = null
                fileDescriptor?.close()
                fileDescriptor = null
                bitmapCache.evictAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
