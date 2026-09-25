package com.qurafy.hamraj37.ui.reader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qurafy.hamraj37.data.model.QuranMetaData
import com.qurafy.hamraj37.data.model.QuranVerse
import com.qurafy.hamraj37.data.model.RevelationType
import com.qurafy.hamraj37.data.model.Surah

@Composable
fun QuranNativeTextView(
    pageIndex: Int,
    getVersesForPage: (Int) -> List<QuranVerse>,
    fontSizeSp: Float = 16f,
    onToggleControls: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val verses = remember(pageIndex) { getVersesForPage(pageIndex) }
    val context = LocalContext.current

    // Determine if page contains start of Surah(s)
    val startingSurahs = remember(verses, pageIndex) {
        verses.map { it.surahNumber }.distinct().mapNotNull { surahNum ->
            val surah = QuranMetaData.getSurahByNumber(surahNum)
            if (surah != null && surah.startPage == pageIndex) surah else null
        }
    }

    if (verses.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable { onToggleControls() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Page $pageIndex",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onToggleControls() })
            },
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 80.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // If there are starting surahs on this page, show Surah Banner & Bismillah Banner
        for (surah in startingSurahs) {
            item(key = "surah_banner_${surah.number}") {
                SurahBannerCard(surah = surah)
            }
            if (surah.number != 9) { // Surah At-Tawbah does not have Bismillah
                item(key = "bismillah_banner_${surah.number}") {
                    BismillahBannerCard()
                }
            }
        }

        // Verse cards
        items(verses, key = { "verse_${it.surahNumber}_${it.verseNumber}_${it.pageNumber}" }) { verse ->
            VerseCard(
                verse = verse,
                fontSizeSp = fontSizeSp,
                onCopyClick = { copyVerseToClipboard(context, verse) },
                onShareClick = { shareVerseText(context, verse) }
            )
        }
    }
}

@Composable
private fun SurahBannerCard(
    surah: Surah,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "سُورَةُ ${surah.nameArabic}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = surah.nameTransliteration + if (surah.nameRomanUrdu.isNotEmpty()) " (${surah.nameRomanUrdu})" else "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${surah.nameEnglish} • ${surah.meaningRomanUrdu}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (surah.revelationType == RevelationType.MECCAN) "Meccan" else "Medinan",
                            fontSize = 11.sp
                        )
                    }
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = "${surah.versesCount} Verses",
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun BismillahBannerCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bismillah ir-Rahman ir-Rahim",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Allah ke naam se shuru jo nihayat meherban aur rahem karne wala hai.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun VerseCard(
    verse: QuranVerse,
    fontSizeSp: Float,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Surah:Verse Badge & Copy/Share Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Surah:Verse Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${verse.surahName ?: "Surah ${verse.surahNumber}"}:${verse.verseNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopyClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy Verse",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onShareClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share Verse",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transliteration Text
            Text(
                text = verse.transliterationText,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = fontSizeSp.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = (fontSizeSp * 1.3f).sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Roman Urdu / Translation Text
            Text(
                text = verse.romanUrduText,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = (fontSizeSp * 0.9f).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = (fontSizeSp * 1.2f).sp
            )
        }
    }
}

private fun copyVerseToClipboard(context: Context, verse: QuranVerse) {
    val textToCopy = formatVerseForShare(verse)
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Quran Verse", textToCopy)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Copied verse to clipboard", Toast.LENGTH_SHORT).show()
}

private fun shareVerseText(context: Context, verse: QuranVerse) {
    val textToShare = formatVerseForShare(verse)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textToShare)
    }
    context.startActivity(Intent.createChooser(intent, "Share Verse"))
}

private fun formatVerseForShare(verse: QuranVerse): String {
    val surahName = verse.surahName ?: "Surah ${verse.surahNumber}"
    return "[$surahName:${verse.verseNumber}]\n\n" +
            "Transliteration: ${verse.transliterationText}\n\n" +
            "Translation: ${verse.romanUrduText}\n\n" +
            "(Qurafy App - Page ${verse.pageNumber})"
}
