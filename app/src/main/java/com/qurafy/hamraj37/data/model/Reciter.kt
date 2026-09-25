package com.qurafy.hamraj37.data.model

import java.util.Locale

data class Reciter(
    val id: String,
    val nameEnglish: String,
    val nameArabic: String,
    val style: String,
    val baseUrl: String
) {
    fun getSurahAudioUrl(surahNumber: Int): String {
        val formattedSurah = String.format(Locale.US, "%03d", surahNumber.coerceIn(1, 114))
        return "$baseUrl$formattedSurah.mp3"
    }

    companion object {
        val DEFAULT = Reciter(
            id = "alafasy",
            nameEnglish = "Mishary Rashid Al-Afasy",
            nameArabic = "مشاري راشد العفاسي",
            style = "Murattal",
            baseUrl = "https://server8.mp3quran.net/afs/"
        )

        val ALL_RECITERS = listOf(
            DEFAULT,
            Reciter(
                id = "basit",
                nameEnglish = "Abdul Basit Abdul Samad",
                nameArabic = "عبد الباسط عبد الصمد",
                style = "Murattal",
                baseUrl = "https://server7.mp3quran.net/basit/"
            ),
            Reciter(
                id = "husary",
                nameEnglish = "Mahmoud Khalil Al-Husary",
                nameArabic = "محمود خليل الحصري",
                style = "Murattal",
                baseUrl = "https://server13.mp3quran.net/hssr/"
            ),
            Reciter(
                id = "ghamdi",
                nameEnglish = "Saad Al-Ghamdi",
                nameArabic = "سعد الغامدي",
                style = "Murattal",
                baseUrl = "https://server7.mp3quran.net/s_gmd/"
            ),
            Reciter(
                id = "shatri",
                nameEnglish = "Abu Bakr Al-Shatri",
                nameArabic = "أبو بكر الشاطري",
                style = "Murattal",
                baseUrl = "https://server11.mp3quran.net/shatri/"
            )
        )

        fun getById(id: String): Reciter {
            return ALL_RECITERS.find { it.id == id } ?: DEFAULT
        }
    }
}
