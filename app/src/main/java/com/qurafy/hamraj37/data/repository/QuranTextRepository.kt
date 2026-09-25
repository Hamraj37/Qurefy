package com.qurafy.hamraj37.data.repository

import com.qurafy.hamraj37.data.dataset.QuranTextDataset
import com.qurafy.hamraj37.data.model.QuranMetaData
import com.qurafy.hamraj37.data.model.QuranVerse
import com.qurafy.hamraj37.data.model.VerseMatch

class QuranTextRepository {

    private val pagesMap: Map<Int, List<QuranVerse>> = QuranTextDataset.getAllVersesByPage()

    fun getVersesForPage(page: Int): List<QuranVerse> {
        if (page !in 1..604) return emptyList()
        return pagesMap[page] ?: emptyList()
    }

    fun searchVerses(query: String): List<VerseMatch> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        val matches = mutableListOf<VerseMatch>()

        for ((page, verses) in pagesMap) {
            for (verse in verses) {
                val surah = QuranMetaData.getSurahByNumber(verse.surahNumber)
                val isMatch = verse.transliterationText.lowercase().contains(q) ||
                        verse.romanUrduText.lowercase().contains(q) ||
                        verse.surahName?.lowercase()?.contains(q) == true ||
                        surah?.nameEnglish?.lowercase()?.contains(q) == true ||
                        surah?.nameRomanUrdu?.lowercase()?.contains(q) == true ||
                        surah?.meaningRomanUrdu?.lowercase()?.contains(q) == true ||
                        surah?.nameArabic?.contains(q) == true

                if (isMatch) {
                    val snippet = "${verse.transliterationText} | ${verse.romanUrduText}"
                    matches.add(
                        VerseMatch(
                            pageNumber = page,
                            surahNumber = verse.surahNumber,
                            verseNumber = verse.verseNumber,
                            surahName = verse.surahName ?: surah?.nameTransliteration,
                            textSnippet = snippet
                        )
                    )
                }
            }
        }
        return matches
    }
}
