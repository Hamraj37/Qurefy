package com.qurafy.hamraj37.data.model

enum class RevelationType {
    MECCAN,
    MEDINAN
}

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameTransliteration: String,
    val nameEnglish: String,
    val startPage: Int,
    val versesCount: Int,
    val revelationType: RevelationType,
    val nameRomanUrdu: String = "",
    val meaningRomanUrdu: String = "",
    val parts: String = ""
)
