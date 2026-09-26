package com.qurafy.hamraj37.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SettingsBrightness
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qurafy.hamraj37.data.model.Juz
import com.qurafy.hamraj37.data.model.QuranMetaData
import com.qurafy.hamraj37.data.model.Reciter
import com.qurafy.hamraj37.data.model.Surah
import com.qurafy.hamraj37.data.repository.NightModePreference
import com.qurafy.hamraj37.ui.audio.AudioPlayerBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    lastReadPage: Int,
    totalPages: Int = 604,
    bookmarkedPages: Set<Int>,
    nightMode: NightModePreference,
    playingSurah: Surah = QuranMetaData.surahs.first(),
    currentReciter: Reciter = Reciter.DEFAULT,
    isPlayingAudio: Boolean = false,
    isBufferingAudio: Boolean = false,
    isAudioPlayerBarVisible: Boolean = false,
    onSetNightMode: (NightModePreference) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onOpenReader: (targetPage: Int) -> Unit,
    onCheckForUpdates: () -> Unit = {},
    onPlaySurah: (Surah) -> Unit = {},
    onTogglePlayPauseAudio: () -> Unit = {},
    onNextSurahAudio: () -> Unit = {},
    onPreviousSurahAudio: () -> Unit = {},
    onOpenAudioSheet: () -> Unit = {},
    onCloseAudioPlayer: () -> Unit = {}
) {
    var showThemeMenu by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Surahs, 1: Juzs, 2: Bookmarks
    var searchQuery by remember { mutableStateOf("") }

    val sortedBookmarkedPages = remember(bookmarkedPages) { bookmarkedPages.sorted() }

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            QuranMetaData.surahs
        } else {
            val q = searchQuery.trim().lowercase()
            QuranMetaData.surahs.filter { surah ->
                surah.number.toString() == q ||
                        surah.nameTransliteration.lowercase().contains(q) ||
                        surah.nameEnglish.lowercase().contains(q) ||
                        surah.nameArabic.contains(q) ||
                        surah.nameRomanUrdu.lowercase().contains(q) ||
                        surah.parts.contains(q)
            }
        }
    }

    val filteredJuzs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            QuranMetaData.juzs
        } else {
            val q = searchQuery.trim().lowercase()
            QuranMetaData.juzs.filter { juz ->
                juz.number.toString() == q ||
                        juz.nameEnglish.lowercase().contains(q) ||
                        juz.nameArabic.contains(q) ||
                        juz.nameRomanUrdu.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                                    contentDescription = "Qurafy Logo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Qurafy",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "القرآن الكريم",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { showThemeMenu = true }) {
                            Icon(
                                imageVector = when (nightMode) {
                                    NightModePreference.DARK -> Icons.Rounded.DarkMode
                                    NightModePreference.LIGHT -> Icons.Rounded.LightMode
                                    NightModePreference.SYSTEM -> Icons.Rounded.SettingsBrightness
                                },
                                contentDescription = "Toggle Theme"
                            )
                        }

                        DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Light Theme") },
                                leadingIcon = { Icon(Icons.Rounded.LightMode, contentDescription = null) },
                                onClick = {
                                    onSetNightMode(NightModePreference.LIGHT)
                                    showThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Night Theme") },
                                leadingIcon = { Icon(Icons.Rounded.DarkMode, contentDescription = null) },
                                onClick = {
                                    onSetNightMode(NightModePreference.DARK)
                                    showThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("System Theme") },
                                leadingIcon = { Icon(Icons.Rounded.SettingsBrightness, contentDescription = null) },
                                onClick = {
                                    onSetNightMode(NightModePreference.SYSTEM)
                                    showThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Check for Updates") },
                                leadingIcon = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null) },
                                onClick = {
                                    showThemeMenu = false
                                    onCheckForUpdates()
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            AudioPlayerBar(
                isVisible = isAudioPlayerBarVisible,
                currentSurah = playingSurah,
                currentReciter = currentReciter,
                isPlaying = isPlayingAudio,
                isBuffering = isBufferingAudio,
                onTogglePlayPause = onTogglePlayPauseAudio,
                onNextSurah = onNextSurahAudio,
                onPreviousSurah = onPreviousSurahAudio,
                onExpandPlayer = onOpenAudioSheet,
                onClosePlayer = onCloseAudioPlayer,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            val glassBorderBrush = remember {
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.70f),
                        Color.White.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Continue Reading Card
                item(key = "last_read_card") {
                    Spacer(modifier = Modifier.height(4.dp))
                    val lastReadSurah = remember(lastReadPage) { QuranMetaData.getSurahForPage(lastReadPage) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { onOpenReader(lastReadPage) },
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.2.dp, glassBorderBrush),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = "السلام عليكم",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Assalamu Alaikum • ${remember { getTimeBasedGreeting() }}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Surah Title & Arabic Name
                            if (lastReadSurah != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = lastReadSurah.nameTransliteration,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = lastReadSurah.nameArabic,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val pageInfoText = if (lastReadSurah != null && lastReadSurah.parts.isNotEmpty()) {
                                    "Page $lastReadPage of $totalPages • Part ${lastReadSurah.parts}"
                                } else {
                                    "Page $lastReadPage of $totalPages"
                                }
                                Text(
                                    text = pageInfoText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                                val percentage = ((lastReadPage.toFloat() / totalPages.coerceAtLeast(1)) * 100).toInt().coerceIn(0, 100)
                                Text(
                                    text = "$percentage%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { (lastReadPage.toFloat() / totalPages.coerceAtLeast(1)).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onOpenReader(lastReadPage) },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    text = "Continue Reading",
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Audio Quran Hero Banner
                item(key = "audio_quran_card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onOpenAudioSheet() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Headphones,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = "Audio Quran Recitation",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${currentReciter.nameEnglish} • 5 Reciters",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Button(
                                onClick = onOpenAudioSheet,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Listen",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // 3. Navigation Tabs (Surahs, Juzs, Bookmarks)
                item(key = "index_tabs") {
                    Column {
                        PrimaryTabRow(
                            selectedTabIndex = selectedTabIndex,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = {
                                    selectedTabIndex = 0
                                    searchQuery = ""
                                },
                                text = { Text("Surahs (${QuranMetaData.surahs.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = {
                                    selectedTabIndex = 1
                                    searchQuery = ""
                                },
                                text = { Text("Juzs (${QuranMetaData.juzs.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTabIndex == 2,
                                onClick = {
                                    selectedTabIndex = 2
                                    searchQuery = ""
                                },
                                text = { Text("Saved (${sortedBookmarkedPages.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )
                        }

                        // Search box for Surahs & Juzs
                        if (selectedTabIndex in 0..1) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = if (selectedTabIndex == 0) "Search Surah name, number, or part..." else "Search Juz number or name..."
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = "Clear Search"
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // 4. Tab Contents
                when (selectedTabIndex) {
                    0 -> { // Surahs Tab
                        if (filteredSurahs.isEmpty()) {
                            item(key = "empty_surahs") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No Surahs found matching '$searchQuery'",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(filteredSurahs, key = { "home_surah_${it.number}" }) { surah ->
                                val isPlayingThisSurah = isPlayingAudio && playingSurah.number == surah.number
                                SurahHomeCard(
                                    surah = surah,
                                    isPlayingThisSurah = isPlayingThisSurah,
                                    onClick = { onOpenReader(surah.startPage) },
                                    onPlayClick = { onPlaySurah(surah) }
                                )
                            }
                        }
                    }

                    1 -> { // Juzs Tab
                        if (filteredJuzs.isEmpty()) {
                            item(key = "empty_juzs") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No Juz found matching '$searchQuery'",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(filteredJuzs, key = { "home_juz_${it.number}" }) { juz ->
                                JuzHomeCard(
                                    juz = juz,
                                    onClick = { onOpenReader(juz.startPage) }
                                )
                            }
                        }
                    }

                    2 -> { // Bookmarks Tab
                        if (sortedBookmarkedPages.isEmpty()) {
                            item(key = "empty_bookmarks") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Rounded.BookmarkBorder,
                                            contentDescription = null,
                                            modifier = Modifier.size(44.dp),
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No saved bookmarks yet",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Tap the bookmark icon in the reader to save pages for quick access",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        } else {
                            items(sortedBookmarkedPages, key = { "home_bookmark_$it" }) { page ->
                                BookmarkHomeCard(
                                    page = page,
                                    onClick = { onOpenReader(page) },
                                    onDeleteBookmark = { onToggleBookmark(page) }
                                )
                            }
                        }
                    }
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SurahHomeCard(
    surah: Surah,
    isPlayingThisSurah: Boolean = false,
    onClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayingThisSurah) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isPlayingThisSurah) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${surah.number}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlayingThisSurah) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title Transliteration & Part
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.nameTransliteration,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (surah.parts.isNotEmpty()) {
                        Text(
                            text = "Part ${surah.parts}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Page ${surah.startPage}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Audio Play Button
            IconButton(onClick = onPlayClick) {
                Icon(
                    imageVector = if (isPlayingThisSurah) Icons.Rounded.GraphicEq else Icons.Rounded.PlayArrow,
                    contentDescription = "Play Surah Recitation",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Arabic Title
            Text(
                text = surah.nameArabic,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun JuzHomeCard(
    juz: Juz,
    onClick: () -> Unit
) {
    val startSurah = QuranMetaData.getSurahForPage(juz.startPage)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Juz Number Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${juz.number}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (juz.nameRomanUrdu.isNotEmpty()) "${juz.nameEnglish} (${juz.nameRomanUrdu})" else juz.nameEnglish,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (startSurah != null) {
                        Text(
                            text = "Starts at ${startSurah.nameTransliteration}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Page ${juz.startPage}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Arabic Title
            Text(
                text = juz.nameArabic,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun BookmarkHomeCard(
    page: Int,
    onClick: () -> Unit,
    onDeleteBookmark: () -> Unit
) {
    val surah = QuranMetaData.getSurahForPage(page)
    val juz = QuranMetaData.getJuzForPage(page)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page Number Badge (styled like Surah number badge)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$page",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Surah Transliteration & Part / Page Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah?.nameTransliteration ?: "Page $page",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (surah != null && surah.parts.isNotEmpty()) {
                        Text(
                            text = "Part ${surah.parts}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (juz != null) {
                        Text(
                            text = "Juz ${juz.number}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Page $page",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Arabic Title & Delete Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (surah != null) {
                    Text(
                        text = surah.nameArabic,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                IconButton(onClick = onDeleteBookmark) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete Bookmark",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun getTimeBasedGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good Morning 🌅"
        in 12..16 -> "Good Afternoon ☀️"
        in 17..21 -> "Good Evening 🌆"
        else -> "Good Night 🌙"
    }
}
