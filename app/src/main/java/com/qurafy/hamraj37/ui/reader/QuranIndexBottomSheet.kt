package com.qurafy.hamraj37.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qurafy.hamraj37.data.model.Juz
import com.qurafy.hamraj37.data.model.QuranMetaData
import com.qurafy.hamraj37.data.model.RevelationType
import com.qurafy.hamraj37.data.model.Surah
import com.qurafy.hamraj37.data.model.VerseMatch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranIndexBottomSheet(
    currentPage: Int,
    bookmarkedPages: Set<Int>,
    initialTabIndex: Int = 0,
    textSearchQuery: String = "",
    textSearchResults: List<VerseMatch> = emptyList(),
    onTextQueryChanged: (String) -> Unit = {},
    onDismissRequest: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onPlaySurah: ((Surah) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var selectedTabIndex by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex.coerceIn(0, 3)) }
    var searchQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.88f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quran Index & Navigation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close Sheet"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tabs (4 tabs: Surahs, Juzs, Bookmarks, Text Search)
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        searchQuery = ""
                    },
                    text = { Text("Surahs (${QuranMetaData.surahs.size})", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        searchQuery = ""
                    },
                    text = { Text("Juzs (${QuranMetaData.juzs.size})", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = {
                        selectedTabIndex = 2
                        searchQuery = ""
                    },
                    text = { Text("Bookmarks (${bookmarkedPages.size})", fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = {
                        selectedTabIndex = 3
                        searchQuery = ""
                    },
                    text = { Text("Text Search", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search input for Surahs & Juzs
            if (selectedTabIndex in 0..1) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (selectedTabIndex == 0) "Search Surah by name or number..." else "Search Juz by number..."
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
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tab Contents
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTabIndex) {
                    0 -> SurahList(
                        searchQuery = searchQuery,
                        currentPage = currentPage,
                        onSurahClick = { surah ->
                            onPageSelected(surah.startPage)
                        },
                        onPlaySurah = onPlaySurah
                    )
                    1 -> JuzList(
                        searchQuery = searchQuery,
                        currentPage = currentPage,
                        onJuzClick = { juz ->
                            onPageSelected(juz.startPage)
                        }
                    )
                    2 -> BookmarksList(
                        bookmarkedPages = bookmarkedPages,
                        currentPage = currentPage,
                        onPageClick = { page ->
                            onPageSelected(page)
                        },
                        onToggleBookmark = onToggleBookmark,
                        onPlaySurah = onPlaySurah
                    )
                    3 -> TextSearchList(
                        textSearchQuery = textSearchQuery,
                        textSearchResults = textSearchResults,
                        onTextQueryChanged = onTextQueryChanged,
                        onPageSelected = onPageSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun TextSearchList(
    textSearchQuery: String,
    textSearchResults: List<VerseMatch>,
    onTextQueryChanged: (String) -> Unit,
    onPageSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = textSearchQuery,
            onValueChange = onTextQueryChanged,
            placeholder = { Text("Search Quran text (e.g. namaz, jannat, sabr)...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (textSearchQuery.isNotEmpty()) {
                    IconButton(onClick = { onTextQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Clear Search"
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (textSearchQuery.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Type keywords above to search Roman Urdu and transliteration text (e.g., namaz, jannat, sabr, fatiha, baqarah)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            Text(
                text = "Search Results (${textSearchResults.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (textSearchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Quran text matches found for '$textSearchQuery'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(textSearchResults) { match ->
                        SearchResultCard(
                            match = match,
                            onClick = { onPageSelected(match.pageNumber) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    match: VerseMatch,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${match.pageNumber}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            val titleText = if (match.surahName != null && match.verseNumber != null) {
                "${match.surahName} - Ayat ${match.verseNumber} (Page ${match.pageNumber})"
            } else if (match.surahName != null) {
                "${match.surahName} (Page ${match.pageNumber})"
            } else {
                "Page ${match.pageNumber}"
            }

            Text(
                text = titleText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = match.textSnippet,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SurahList(
    searchQuery: String,
    currentPage: Int,
    onSurahClick: (Surah) -> Unit,
    onPlaySurah: ((Surah) -> Unit)? = null
) {
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
                        surah.meaningRomanUrdu.lowercase().contains(q)
            }
        }
    }

    if (filteredSurahs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Surahs found matching '$searchQuery'",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredSurahs, key = { it.number }) { surah ->
                val currentSurah = QuranMetaData.getSurahForPage(currentPage)
                val isSelected = currentSurah?.number == surah.number

                SurahItem(
                    surah = surah,
                    isSelected = isSelected,
                    onClick = { onSurahClick(surah) },
                    onPlayClick = if (onPlaySurah != null) { { onPlaySurah(surah) } } else null
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun SurahItem(
    surah: Surah,
    isSelected: Boolean,
    onClick: () -> Unit,
    onPlayClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Surah Number Badge
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${surah.number}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // English & Roman Urdu Names & Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (surah.nameRomanUrdu.isNotEmpty()) "${surah.nameTransliteration} (${surah.nameRomanUrdu})" else surah.nameTransliteration,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            val detailText = buildString {
                if (surah.parts.isNotEmpty()) append("Part ${surah.parts} • ")
                append("${surah.versesCount} Verses")
                if (surah.meaningRomanUrdu.isNotEmpty()) append(" • ${surah.meaningRomanUrdu}")
            }
            Text(
                text = detailText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (onPlayClick != null) {
            IconButton(onClick = onPlayClick) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Play Surah Recitation",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Arabic Name & Page Number
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = surah.nameArabic,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(
                    onClick = onClick,
                    label = {
                        Text(
                            text = if (surah.revelationType == RevelationType.MECCAN) "Meccan" else "Medinan",
                            fontSize = 10.sp
                        )
                    },
                    modifier = Modifier.height(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Page ${surah.startPage}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun JuzList(
    searchQuery: String,
    currentPage: Int,
    onJuzClick: (Juz) -> Unit
) {
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

    if (filteredJuzs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Juz found matching '$searchQuery'",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredJuzs, key = { it.number }) { juz ->
                val currentJuz = QuranMetaData.getJuzForPage(currentPage)
                val isSelected = currentJuz?.number == juz.number

                JuzItem(
                    juz = juz,
                    isSelected = isSelected,
                    onClick = { onJuzClick(juz) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun JuzItem(
    juz: Juz,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val startSurah = QuranMetaData.getSurahForPage(juz.startPage)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Juz Badge
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.tertiaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${juz.number}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (juz.nameRomanUrdu.isNotEmpty()) "${juz.nameEnglish} • ${juz.nameRomanUrdu}" else juz.nameEnglish,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Starts at ${startSurah?.nameTransliteration ?: ""} (${startSurah?.nameArabic ?: ""})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Arabic & Page
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = juz.nameArabic,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Page ${juz.startPage}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun BookmarksList(
    bookmarkedPages: Set<Int>,
    currentPage: Int,
    onPageClick: (Int) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onPlaySurah: ((Surah) -> Unit)? = null
) {
    if (bookmarkedPages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
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
                    text = "Tap the bookmark icon on top bar while reading to save pages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    } else {
        val sortedPages = remember(bookmarkedPages) { bookmarkedPages.sorted() }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(sortedPages, key = { it }) { page ->
                val surah = QuranMetaData.getSurahForPage(page)
                val juz = QuranMetaData.getJuzForPage(page)
                val isSelected = currentPage == page

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable { onPageClick(page) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
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

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Page $page • ${surah?.nameTransliteration ?: ""}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Juz ${juz?.number ?: "-"} • Surah ${surah?.number ?: "-"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (onPlaySurah != null && surah != null) {
                        IconButton(onClick = { onPlaySurah(surah) }) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Play Surah Recitation",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    IconButton(onClick = { onToggleBookmark(page) }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete Bookmark",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}
