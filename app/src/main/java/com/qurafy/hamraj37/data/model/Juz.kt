package com.qurafy.hamraj37.data.model

data class Juz(
    val number: Int,
    val startPage: Int,
    val nameArabic: String = "الجزء $number",
    val nameEnglish: String = "Juz $number",
    val nameRomanUrdu: String = ""
)
