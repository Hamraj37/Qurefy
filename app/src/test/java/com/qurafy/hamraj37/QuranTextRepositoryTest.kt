package com.qurafy.hamraj37

import com.qurafy.hamraj37.data.repository.QuranTextRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuranTextRepositoryTest {

    private lateinit var repository: QuranTextRepository

    @Before
    fun setUp() {
        repository = QuranTextRepository()
    }

    @Test
    fun testAll604PagesPopulatedWithAuthenticVerseData() {
        for (page in 1..604) {
            val verses = repository.getVersesForPage(page)
            assertNotNull("Verses for page $page should not be null", verses)
            assertFalse("Verses for page $page should not be empty", verses.isEmpty())
            assertTrue(
                "All verses on page $page should have pageNumber == $page",
                verses.all { it.pageNumber == page }
            )
            assertTrue(
                "All verses on page $page should have non-blank transliteration text",
                verses.all { it.transliterationText.isNotBlank() }
            )
            assertTrue(
                "All verses on page $page should have non-blank Roman Urdu text",
                verses.all { it.romanUrduText.isNotBlank() }
            )
        }
    }

    @Test
    fun testInvalidPageNumbers() {
        assertTrue("Page 0 should return empty list", repository.getVersesForPage(0).isEmpty())
        assertTrue("Page 605 should return empty list", repository.getVersesForPage(605).isEmpty())
        assertTrue("Page -1 should return empty list", repository.getVersesForPage(-1).isEmpty())
    }

    @Test
    fun testVerificationSurah1AlFatihah() {
        val verses = repository.getVersesForPage(1)
        assertEquals(7, verses.size)
        assertTrue("All verses on page 1 should belong to Surah 1", verses.all { it.surahNumber == 1 })
        assertEquals("Fātiha", verses.first().surahName)
        assertEquals(1, verses.first().verseNumber)
        assertEquals(7, verses.last().verseNumber)

        val v1 = verses.find { it.verseNumber == 1 }!!
        assertEquals("Bismillah ir-Rahman ir-Rahim", v1.transliterationText)
        assertTrue(v1.romanUrduText.contains("meherban", ignoreCase = true))

        val v7 = verses.find { it.verseNumber == 7 }!!
        assertTrue(v7.transliterationText.contains("Siratalladhina", ignoreCase = true))
        assertTrue(v7.romanUrduText.contains("in'aam", ignoreCase = true))
    }

    @Test
    fun testVerificationSurah2AlBaqarah() {
        val page2Verses = repository.getVersesForPage(2)
        assertFalse(page2Verses.isEmpty())
        val firstVerse = page2Verses.first()
        assertEquals(2, firstVerse.surahNumber)
        assertEquals("Al-Baqarah", firstVerse.surahName)
        assertEquals(1, firstVerse.verseNumber)
        assertEquals("Alif-Lam-Mim", firstVerse.transliterationText)

        // Verse 3 (contains "namaz")
        val v3 = page2Verses.find { it.verseNumber == 3 }
        assertNotNull("Surah 2 Ayat 3 should exist on page 2", v3)
        assertTrue(v3!!.romanUrduText.contains("namaz", ignoreCase = true))

        // Verse 25 (contains "jannat")
        val allVerses = (1..604).flatMap { repository.getVersesForPage(it) }
        val v25 = allVerses.find { it.surahNumber == 2 && it.verseNumber == 25 }
        assertNotNull("Surah 2 Ayat 25 should exist", v25)
        assertTrue(v25!!.romanUrduText.contains("jannat", ignoreCase = true))

        // Verse 255 (Ayat al-Kursi)
        val ayatKursi = allVerses.find { it.surahNumber == 2 && it.verseNumber == 255 }
        assertNotNull("Surah 2 Ayat 255 (Ayat al-Kursi) should exist", ayatKursi)
        assertTrue(ayatKursi!!.transliterationText.contains("Hayyul-Qayyum", ignoreCase = true))
    }

    @Test
    fun testVerificationSurah36YaSin() {
        val page431Verses = repository.getVersesForPage(431)
        assertFalse(page431Verses.isEmpty())

        val yasinVerse1 = page431Verses.find { it.surahNumber == 36 && it.verseNumber == 1 }
        assertNotNull("Surah 36 Ayat 1 should exist on page 431", yasinVerse1)
        assertEquals("Ya-Sin", yasinVerse1!!.transliterationText)

        val yasinVerse2 = page431Verses.find { it.surahNumber == 36 && it.verseNumber == 2 }
        assertNotNull("Surah 36 Ayat 2 should exist on page 431", yasinVerse2)
        assertEquals("Wal-Qur'anil-Hakim", yasinVerse2!!.transliterationText)

        // Verse 58
        val allVerses = (1..604).flatMap { repository.getVersesForPage(it) }
        val verse58 = allVerses.find { it.surahNumber == 36 && it.verseNumber == 58 }
        assertNotNull("Surah 36 Ayat 58 should exist", verse58)
        assertTrue(verse58!!.transliterationText.contains("Salamun qawlam", ignoreCase = true))
        assertTrue(verse58.romanUrduText.contains("Meherban", ignoreCase = true))
    }

    @Test
    fun testVerificationSurah67AlMulk() {
        val page554Verses = repository.getVersesForPage(554)
        assertFalse(page554Verses.isEmpty())

        val mulkVerse1 = page554Verses.find { it.surahNumber == 67 && it.verseNumber == 1 }
        assertNotNull("Surah 67 Ayat 1 should exist on page 554", mulkVerse1)
        assertTrue(mulkVerse1!!.transliterationText.contains("Tabarakalladhi", ignoreCase = true))
        assertTrue(mulkVerse1.romanUrduText.contains("badshahi", ignoreCase = true))
    }

    @Test
    fun testVerificationSurah112AlIkhlas() {
        val allVerses = (1..604).flatMap { repository.getVersesForPage(it) }

        val ikhlas1 = allVerses.find { it.surahNumber == 112 && it.verseNumber == 1 }
        assertNotNull("Surah 112 Ayat 1 should exist", ikhlas1)
        assertEquals("Qul Huwallahu Ahad", ikhlas1!!.transliterationText)
        assertEquals("Kaho ke woh Allah ek hai.", ikhlas1.romanUrduText)

        val ikhlas2 = allVerses.find { it.surahNumber == 112 && it.verseNumber == 2 }
        assertNotNull("Surah 112 Ayat 2 should exist", ikhlas2)
        assertEquals("Allahus-Samad", ikhlas2!!.transliterationText)
    }

    @Test
    fun testVerificationSurah113AlFalaq() {
        val allVerses = (1..604).flatMap { repository.getVersesForPage(it) }

        val falaq1 = allVerses.find { it.surahNumber == 113 && it.verseNumber == 1 }
        assertNotNull("Surah 113 Ayat 1 should exist", falaq1)
        assertEquals("Qul a'udhu birabbil-falaq", falaq1!!.transliterationText)

        val falaq5 = allVerses.find { it.surahNumber == 113 && it.verseNumber == 5 }
        assertNotNull("Surah 113 Ayat 5 should exist", falaq5)
        assertEquals("Wa min sharri hasidin idha hasad", falaq5!!.transliterationText)
    }

    @Test
    fun testVerificationSurah114AnNas() {
        val allVerses = (1..604).flatMap { repository.getVersesForPage(it) }

        val nas1 = allVerses.find { it.surahNumber == 114 && it.verseNumber == 1 }
        assertNotNull("Surah 114 Ayat 1 should exist", nas1)
        assertEquals("Qul a'udhu birabbin-nas", nas1!!.transliterationText)

        val nas6 = allVerses.find { it.surahNumber == 114 && it.verseNumber == 6 }
        assertNotNull("Surah 114 Ayat 6 should exist", nas6)
        assertEquals("Minal-jinnati wan-nas", nas6!!.transliterationText)
    }

    @Test
    fun testSearchQueryMatchingRomanUrduTerms() {
        val jannatResults = repository.searchVerses("jannat")
        assertFalse("Search for 'jannat' should return results", jannatResults.isEmpty())

        val namazResults = repository.searchVerses("namaz")
        assertFalse("Search for 'namaz' should return results", namazResults.isEmpty())

        val sabrResults = repository.searchVerses("sabr")
        assertFalse("Search for 'sabr' should return results", sabrResults.isEmpty())

        val meherbanResults = repository.searchVerses("meherban")
        assertFalse("Search for 'meherban' should return results", meherbanResults.isEmpty())

        val emptyResults = repository.searchVerses("")
        assertTrue("Blank search query should return empty list", emptyResults.isEmpty())
    }
}
