package com.qurafy.hamraj37

import com.qurafy.hamraj37.data.model.QuranMetaData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HomeNavigationTest {

    @Test
    fun testSurahAndJuzIndexingForHomeScreen() {
        assertEquals(114, QuranMetaData.surahs.size)
        assertEquals(30, QuranMetaData.juzs.size)

        // Verify Surah 36 Ya-Sin starts at page 431
        val yaseen = QuranMetaData.getSurahByNumber(36)
        assertNotNull(yaseen)
        assertEquals(431, yaseen?.startPage)

        // Verify Sipara 30 starts at page 578
        val sipara30 = QuranMetaData.getJuzByNumber(30)
        assertNotNull(sipara30)
        assertEquals(578, sipara30?.startPage)
    }
}
