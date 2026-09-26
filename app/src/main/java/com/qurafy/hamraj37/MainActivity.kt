package com.qurafy.hamraj37

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.qurafy.hamraj37.data.model.Reciter
import com.qurafy.hamraj37.data.repository.NightModePreference
import com.qurafy.hamraj37.ui.audio.AudioPlayerSheet
import com.qurafy.hamraj37.ui.home.HomeScreen
import com.qurafy.hamraj37.ui.reader.QuranReaderScreen
import com.qurafy.hamraj37.ui.reader.QuranReaderViewModel
import com.qurafy.hamraj37.ui.theme.QurafyTheme
import com.qurafy.hamraj37.ui.update.AppUpdateDialog

enum class ScreenDestination {
    HOME,
    READER
}

class MainActivity : ComponentActivity() {

    private val viewModel: QuranReaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val nightMode by viewModel.nightMode.collectAsState()
            val lastReadPage by viewModel.lastReadPage.collectAsState()
            val totalPages by viewModel.pageCount.collectAsState()
            val bookmarkedPages by viewModel.bookmarkedPages.collectAsState()
            val availableUpdate by viewModel.availableUpdate.collectAsState()

            // Audio Player State
            val playingSurah by viewModel.playingSurah.collectAsState()
            val currentReciter by viewModel.currentReciter.collectAsState()
            val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
            val isAudioBuffering by viewModel.isAudioBuffering.collectAsState()
            val isAudioPlayerBarVisible by viewModel.isAudioPlayerBarVisible.collectAsState()
            val isAudioSheetOpen by viewModel.isAudioSheetOpen.collectAsState()
            val audioPositionMs by viewModel.audioPositionMs.collectAsState()
            val audioDurationMs by viewModel.audioDurationMs.collectAsState()
            val playbackSpeed by viewModel.playbackSpeed.collectAsState()

            var currentDestination by remember { mutableStateOf(ScreenDestination.HOME) }
            var targetReaderPage by remember { mutableIntStateOf(lastReadPage) }

            LaunchedEffect(lastReadPage) {
                if (currentDestination == ScreenDestination.HOME) {
                    targetReaderPage = lastReadPage
                }
            }

            val isDark = when (nightMode) {
                NightModePreference.DARK -> true
                NightModePreference.LIGHT -> false
                NightModePreference.SYSTEM -> isSystemInDarkTheme()
            }

            QurafyTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentDestination) {
                        ScreenDestination.HOME -> {
                            HomeScreen(
                                lastReadPage = lastReadPage,
                                totalPages = totalPages,
                                bookmarkedPages = bookmarkedPages,
                                nightMode = nightMode,
                                playingSurah = playingSurah,
                                currentReciter = currentReciter,
                                isPlayingAudio = isAudioPlaying,
                                isBufferingAudio = isAudioBuffering,
                                isAudioPlayerBarVisible = isAudioPlayerBarVisible,
                                onSetNightMode = { mode -> viewModel.setNightMode(mode) },
                                onToggleBookmark = { page -> viewModel.toggleBookmark(page) },
                                onOpenReader = { targetPage ->
                                    viewModel.saveLastReadPage(targetPage)
                                    targetReaderPage = targetPage
                                    currentDestination = ScreenDestination.READER
                                },
                                onCheckForUpdates = {
                                    viewModel.checkForUpdates(isManual = true)
                                },
                                onPlaySurah = { surah ->
                                    viewModel.playSurah(surah)
                                },
                                onTogglePlayPauseAudio = {
                                    viewModel.toggleAudioPlayPause()
                                },
                                onNextSurahAudio = {
                                    viewModel.nextSurahAudio()
                                },
                                onPreviousSurahAudio = {
                                    viewModel.previousSurahAudio()
                                },
                                onOpenAudioSheet = {
                                    viewModel.setAudioSheetOpen(true)
                                },
                                onCloseAudioPlayer = {
                                    viewModel.closeAudioPlayer()
                                }
                            )
                        }

                        ScreenDestination.READER -> {
                            QuranReaderScreen(
                                initialPage = targetReaderPage,
                                viewModel = viewModel,
                                onNavigateHome = {
                                    currentDestination = ScreenDestination.HOME
                                }
                            )
                        }
                    }

                    // Render Audio Player Sheet when open
                    if (isAudioSheetOpen) {
                        AudioPlayerSheet(
                            currentSurah = playingSurah,
                            currentReciter = currentReciter,
                            isPlaying = isAudioPlaying,
                            isBuffering = isAudioBuffering,
                            currentPositionMs = audioPositionMs,
                            durationMs = audioDurationMs,
                            playbackSpeed = playbackSpeed,
                            allReciters = Reciter.ALL_RECITERS,
                            onDismissRequest = { viewModel.setAudioSheetOpen(false) },
                            onTogglePlayPause = { viewModel.toggleAudioPlayPause() },
                            onSeekTo = { pos -> viewModel.seekAudioTo(pos) },
                            onNextSurah = { viewModel.nextSurahAudio() },
                            onPreviousSurah = { viewModel.previousSurahAudio() },
                            onSelectReciter = { reciter -> viewModel.selectReciter(reciter) },
                            onSetPlaybackSpeed = { speed -> viewModel.setPlaybackSpeed(speed) },
                            onJumpToSurahPage = { page ->
                                viewModel.saveLastReadPage(page)
                                targetReaderPage = page
                                currentDestination = ScreenDestination.READER
                            }
                        )
                    }

                    // Toast message for update status
                    val context = LocalContext.current
                    val updateCheckMessage by viewModel.updateCheckMessage.collectAsState()
                    LaunchedEffect(updateCheckMessage) {
                        updateCheckMessage?.let { msg ->
                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                            viewModel.dismissUpdateMessage()
                        }
                    }

                    // Render Update Dialog if available
                    availableUpdate?.let { updateInfo ->
                        AppUpdateDialog(
                            updateInfo = updateInfo,
                            onDismiss = { viewModel.dismissUpdateDialog() },
                            onDownloadUpdate = { downloadUrl ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                context.startActivity(intent)
                                viewModel.dismissUpdateDialog()
                            }
                        )
                    }
                }
            }
        }
    }
}
