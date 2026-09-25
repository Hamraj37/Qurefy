package com.qurafy.hamraj37

import com.qurafy.hamraj37.data.model.QuranMetaData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class QuranMetaDataTest {

    @Test
    fun verifySurahCountAndOrdering() {
        assertEquals(114, QuranMetaData.surahs.size)
        assertEquals(1, QuranMetaData.surahs.first().number)
        assertEquals(114, QuranMetaData.surahs.last().number)
        assertEquals("Al-Fatiha", QuranMetaData.surahs.first().nameTransliteration)
        assertEquals("An-Nas", QuranMetaData.surahs.last().nameTransliteration)
    }

    @Test
    fun verifyJuzCountAndOrdering() {
        assertEquals(30, QuranMetaData.juzs.size)
        assertEquals(1, QuranMetaData.juzs.first().number)
        assertEquals(30, QuranMetaData.juzs.last().number)
        assertEquals(1, QuranMetaData.juzs.first().startPage)
        assertEquals(582, QuranMetaData.juzs.last().startPage)
    }

    @Test
    fun verifySurahAndJuzPageMapping() {
        // Page 1 should map to Al-Fatiha and Juz 1
        val surahPage1 = QuranMetaData.getSurahForPage(1)
        val juzPage1 = QuranMetaData.getJuzForPage(1)
        assertNotNull(surahPage1)
        assertNotNull(juzPage1)
        assertEquals(1, surahPage1?.number)
        assertEquals(1, juzPage1?.number)

        // Page 50 should map to Ali 'Imran (starts at 50) and Juz 3 (starts at 42)
        val surahPage50 = QuranMetaData.getSurahForPage(50)
        val juzPage50 = QuranMetaData.getJuzForPage(50)
        assertEquals(3, surahPage50?.number)
        assertEquals(3, juzPage50?.number)

        // Page 604 should map to An-Nas (or latest Surah starting <= 604) and Juz 30
        val surahPage604 = QuranMetaData.getSurahForPage(604)
        val juzPage604 = QuranMetaData.getJuzForPage(604)
        assertEquals(114, surahPage604?.number)
        assertEquals(30, juzPage604?.number)

        // Invalid pages return null
        assertNull(QuranMetaData.getSurahForPage(0))
        assertNull(QuranMetaData.getSurahForPage(605))
        assertNull(QuranMetaData.getJuzForPage(0))
        assertNull(QuranMetaData.getJuzForPage(605))
    }

    @Test
    fun verifyRomanUrduFieldsForSurahsAndJuzs() {
        assertEquals(114, QuranMetaData.surahs.size)
        assertEquals(30, QuranMetaData.juzs.size)

        // Verify all 114 surahs have non-empty Roman Urdu fields
        for (surah in QuranMetaData.surahs) {
            org.junit.Assert.assertTrue(
                "Surah ${surah.number} nameRomanUrdu should not be empty",
                surah.nameRomanUrdu.isNotBlank()
            )
            org.junit.Assert.assertTrue(
                "Surah ${surah.number} meaningRomanUrdu should not be empty",
                surah.meaningRomanUrdu.isNotBlank()
            )
        }

        // Verify all 30 juzs have non-empty Roman Urdu fields
        for (juz in QuranMetaData.juzs) {
            org.junit.Assert.assertTrue(
                "Juz ${juz.number} nameRomanUrdu should not be empty",
                juz.nameRomanUrdu.isNotBlank()
            )
        }

        // Specific checks
        val firstSurah = QuranMetaData.surahs.first()
        assertEquals("Fatiha", firstSurah.nameRomanUrdu)
        assertEquals("Aaghaz / Shuruat", firstSurah.meaningRomanUrdu)

        val lastSurah = QuranMetaData.surahs.last()
        assertEquals("An-Nas", lastSurah.nameRomanUrdu)
        assertEquals("Insaan / Log", lastSurah.meaningRomanUrdu)

        assertEquals("Pehla Sipara", QuranMetaData.juzs.first().nameRomanUrdu)
        assertEquals("Teeswa Sipara", QuranMetaData.juzs.last().nameRomanUrdu)
    }
}
