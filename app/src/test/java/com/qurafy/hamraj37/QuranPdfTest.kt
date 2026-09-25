package com.qurafy.hamraj37

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class QuranPdfTest {

    @Test
    fun verifyPdfAssetExists() {
        val assetPdf = File("src/main/assets/quran-roman-urdu-hindi.pdf")
        assertTrue("PDF asset must exist in src/main/assets", assetPdf.exists())
        assertTrue("PDF asset size should be greater than 0", assetPdf.length() > 0)
    }
}
