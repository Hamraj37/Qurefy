package com.qurafy.hamraj37.data.model

data class QuranVerse(
    val surahNumber: Int,
    val verseNumber: Int,
    val pageNumber: Int,
    val transliterationText: String,
    val romanUrduText: String,
    val surahName: String? = null
)
