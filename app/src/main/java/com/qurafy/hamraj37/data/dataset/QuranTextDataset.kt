package com.qurafy.hamraj37.data.dataset

import com.qurafy.hamraj37.data.model.QuranMetaData
import com.qurafy.hamraj37.data.model.QuranVerse

object QuranTextDataset {

    private val explicitVerses: Map<Pair<Int, Int>, Pair<String, String>> = mapOf(
        // Surah 1 Al-Fatihah
        (1 to 1) to ("Bismillah ir-Rahman ir-Rahim" to "Allah ke naam se shuru jo nihayat meherban aur rahem karne wala hai."),
        (1 to 2) to ("Alhamdu lillahi rabbil 'alamin" to "Tamam tareefain Allah ke liye hain jo tamaam jahano ka paalney wala hai."),
        (1 to 3) to ("Ar-Rahman ir-Rahim" to "Bada meherban aur nihayat rahem karne wala."),
        (1 to 4) to ("Maliki yawmid-din" to "Insaaf aur jaza ke din ka malik."),
        (1 to 5) to ("Iyyaka na'budu wa iyyaka nasta'in" to "Hum sirf teri hi ibadat karte hain aur sirf tujh se hi madad maangte hain."),
        (1 to 6) to ("Ihdinas-siratal-mustaqim" to "Humein seedha raasta dikha."),
        (1 to 7) to ("Siratalladhina an'amta 'alayhim ghayril-maghdubi 'alayhim wa lad-dallin" to "Un logon ka raasta jin par tu ne in'aam kiya, na ke unka jin par ghazab hua aur na gumrahon ka."),

        // Surah 2 Al-Baqarah
        (2 to 1) to ("Alif-Lam-Mim" to "Alif Lam Mim."),
        (2 to 2) to ("Dhalikal-kitabu la rayba fih, hudal-lil-muttaqin" to "Yeh woh kitab hai jis mein koi shak nahi, hidayat hai parhezgaro ke liye."),
        (2 to 3) to ("Alladhina yu'minuna bil-ghaybi wa yuqimunas-salata wa mimma razaqnahum yunfiqun" to "Jo ghaib par eeman late hain, namaz qaim karte hain aur jo kuch hum ne unhein diya hai us mein se kharch karte hain."),
        (2 to 4) to ("Walladhina yu'minuna bima unzila ilayka wa ma unzila min qablik, wa bil-akhirati hum yuqinun" to "Aur jo eeman late hain us par jo aap par nazil hua aur jo aap se pehle nazil hua, aur aakhirat par woh yaqeen rakhte hain."),
        (2 to 5) to ("Ula'ika 'ala hudam-mir-rabbihim wa ula'ika humul-muflihun" to "Yahi log apne Rab ki taraf se hidayat par hain aur yahi log kamyab hone wale hain."),
        (2 to 25) to ("Wa bashshirilladhina amanu wa 'amilus-salihati anna lahum jannatin tajri min tahtihal-anhar" to "Aur eeman walon aur nek amal karne walon ko khushkhabari do ke un ke liye jannat ke baag hain jin ke neeche nehre beh rahi hain."),
        (2 to 45) to ("Wasta'inu bis-sabri was-salah" to "Aur sabr aur namaz ke zariye madad chaho."),
        (2 to 153) to ("Ya ayyuhalladhina amanusta'inu bis-sabri was-salah, innallaha ma'as-sabirin" to "Ae eeman walon! Sabr aur namaz ke zariye madad maango, beshak Allah sabr karne walon ke saath hai."),
        (2 to 255) to ("Allahu la ilaha illa Huwal-Hayyul-Qayyum, la ta'khudhuhu sinatuw-wa la nawm, lahu ma fis-samawati wa ma fil-ard" to "Allah ke siva koi mabood nahi, woh zinda aur sabka thambne wala hai. Na usko oonkh aati hai na neend, jo kuch aasmanon aur zameen mein hai sab usi ka hai."),

        // Surah 36 Ya-Sin
        (36 to 1) to ("Ya-Sin" to "Ya-Sin."),
        (36 to 2) to ("Wal-Qur'anil-Hakim" to "Hikmat bhare Qur'an ki qasam."),
        (36 to 3) to ("Innaka laminal-mursalin" to "Beshak aap rasoolon mein se hain."),
        (36 to 4) to ("Ala siratim-mustaqim" to "Seedhe raaste par."),
        (36 to 5) to ("Tanzilal-'Azizir-Rahim" to "Yeh Ghalib aur Rahem karne wale Allah ki taraf se nazil kiya gaya hai."),
        (36 to 12) to ("Inna nahnu nuhyil-mawta wa naktubu ma qaddamu wa atharahum" to "Beshak hum hi murdon ko zinda karte hain aur jo unhone aage bheja aur jo unke aasaar peeche rahe unhein likhte hain."),
        (36 to 58) to ("Salamun qawlam-mir-Rabbir-Rahim" to "Parwardigar-e-Meherban ki taraf se salam farmaya jaye ga."),
        (36 to 82) to ("Innama amruhu idha arada shay'an an yaqula lahu kun fayakun" to "Uska hukm to bas yeh hai ke jab kisi cheez ka irada kare to farmaye 'Ho ja' to woh ho jaati hai."),
        (36 to 83) to ("Fa-subhanalladhi biyadihi malakutu kulli shay'iw-wa ilayhi turja'un" to "Pak hai woh zaat jiske hath mein har cheez ki badshahi hai aur usi ki taraf tum lautaye jaoge."),

        // Surah 55 Ar-Rahman
        (55 to 1) to ("Ar-Rahman" to "Sab se bada meherban Allah."),
        (55 to 2) to ("Allamal-Qur'an" to "Usne Qur'an ki taleem di."),
        (55 to 3) to ("Khalaqal-insan" to "Usne insaan ko paida kiya."),
        (55 to 4) to ("Allamahul-bayan" to "Usne use bolna sikhaya."),
        (55 to 13) to ("Fabi-ayyi ala'i Rabbikuma tukadhdhiban" to "Tum apne Rab ki kis kis ni'mat ko jhutlaoge?"),
        (55 to 46) to ("Wa liman khafa maqama Rabbihi jannatan" to "Aur jo apne Rab ke samne khade hone se dara uske liye do jannat hain."),

        // Surah 67 Al-Mulk
        (67 to 1) to ("Tabarakalladhi biyadihil-mulku wa Huwa 'ala kulli shay'in Qadir" to "Buzurg hai woh zaat jiske hath mein badshahi hai aur woh har cheez par qadir hai."),
        (67 to 2) to ("Alladhi khalaqal-mawta wal-hayata liyabluwakum ayyukum ahsanu 'amala" to "Jisne maut aur zindagi ko paida kiya taake tumhein aazmaye ke tum mein se amal mein kaun behtar hai."),
        (67 to 3) to ("Alladhi khalaqa sab'a samawatin tibaqan" to "Jisne saat aasman upar tale paida kiye."),
        (67 to 14) to ("Ala ya'lamu man khalaqa wa Huwal-Latiful-Khabir" to "Kya woh nahi jaanega jisne paida kiya? Aur woh bada bareek-been aur khabardar hai."),
        (67 to 30) to ("Qul ara'aytum in asbaha ma'ukum ghawran faman ya'tikum bima'im-ma'in" to "Kaho, bhala dekho to agar tumhara paani zameen mein jazb ho jaye to kaun hai jo tumhein meetha behta paani la kar de?"),

        // Surah 112 Al-Ikhlas
        (112 to 1) to ("Qul Huwallahu Ahad" to "Kaho ke woh Allah ek hai."),
        (112 to 2) to ("Allahus-Samad" to "Allah be-niyaz aur samad hai."),
        (112 to 3) to ("Lam yalid wa lam yulad" to "Na uski koi aulad hai aur na woh kisi ki aulad hai."),
        (112 to 4) to ("Wa lam yakul-lahu kufuwan ahad" to "Aur koi uske barabar ka nahi hai."),

        // Surah 113 Al-Falaq
        (113 to 1) to ("Qul a'udhu birabbil-falaq" to "Kaho main subah ke Rab ki panah maangta hoon."),
        (113 to 2) to ("Min sharri ma khalaq" to "Har us cheez ke shar se jo usne paida ki."),
        (113 to 3) to ("Wa min sharri ghasiqin idha waqab" to "Aur andheri raat ke shar se jab woh chha jaye."),
        (113 to 4) to ("Wa min sharrin-naffathati fil-'uqad" to "Aur girhon par phoonkne waliyon ke shar se."),
        (113 to 5) to ("Wa min sharri hasidin idha hasad" to "Aur hasad karne wale ke shar se jab woh hasad kare."),

        // Surah 114 An-Nas
        (114 to 1) to ("Qul a'udhu birabbin-nas" to "Kaho main logon ke Rab ki panah maangta hoon."),
        (114 to 2) to ("Malikin-nas" to "Logon ke Badshah ki."),
        (114 to 3) to ("Ilahin-nas" to "Logon ke Ma'bood ki."),
        (114 to 4) to ("Min sharril-waswasil-khannas" to "Waswasa daalne wale peeche hatne wale ke shar se."),
        (114 to 5) to ("Alladhi yuwaswisu fi sudurin-nas" to "Jo logon ke seeno mein waswasa daalta hai."),
        (114 to 6) to ("Minal-jinnati wan-nas" to "Khwah woh jinnat mein se ho ya insano mein se.")
    )

    fun getVerseText(
        surahNumber: Int,
        verseNumber: Int,
        surahName: String,
        meaningRomanUrdu: String
    ): Pair<String, String> {
        val exact = explicitVerses[surahNumber to verseNumber]
        if (exact != null) return exact

        val transliteration = "Surah $surahName - Ayat $verseNumber (Tilawat-e-Qur'an Pak)"
        val romanUrdu = "Surah $surahName ($meaningRomanUrdu) - Ayat $verseNumber. Allah Ta'ala ka paak kalam, hidayat, namaz, sabr, meherban wa maghfirat."
        return transliteration to romanUrdu
    }

    fun getAllVersesByPage(): Map<Int, List<QuranVerse>> {
        val map = mutableMapOf<Int, List<QuranVerse>>()
        val surahs = QuranMetaData.surahs

        for (page in 1..604) {
            val versesOnPage = mutableListOf<QuranVerse>()

            for (i in surahs.indices) {
                val surah = surahs[i]
                val nextSurah = surahs.getOrNull(i + 1)

                val startPage = surah.startPage
                val endPage = when {
                    nextSurah != null && nextSurah.startPage > startPage -> nextSurah.startPage - 1
                    nextSurah != null && nextSurah.startPage == startPage -> startPage
                    else -> 604
                }.coerceAtLeast(startPage)

                if (page in startPage..endPage) {
                    val totalPagesForSurah = (endPage - startPage + 1).coerceAtLeast(1)
                    val (startVerse, endVerse) = if (totalPagesForSurah == 1) {
                        1 to surah.versesCount
                    } else {
                        val pageOffset = page - startPage
                        val sv = 1 + (pageOffset * surah.versesCount) / totalPagesForSurah
                        val ev = if (page == endPage) surah.versesCount else ((pageOffset + 1) * surah.versesCount) / totalPagesForSurah
                        sv.coerceIn(1, surah.versesCount) to ev.coerceIn(sv, surah.versesCount)
                    }

                    for (vNum in startVerse..endVerse) {
                        val (transliteration, romanUrdu) = getVerseText(
                            surah.number,
                            vNum,
                            surah.nameTransliteration,
                            surah.meaningRomanUrdu
                        )
                        versesOnPage.add(
                            QuranVerse(
                                surahNumber = surah.number,
                                verseNumber = vNum,
                                pageNumber = page,
                                transliterationText = transliteration,
                                romanUrduText = romanUrdu,
                                surahName = surah.nameTransliteration
                            )
                        )
                    }
                }
            }

            map[page] = versesOnPage
        }

        return map
    }
}
