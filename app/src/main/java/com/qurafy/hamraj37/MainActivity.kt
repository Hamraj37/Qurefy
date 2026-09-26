package com.qurafy.hamraj37

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
import com.qurafy.hamraj37.data.repository.NightModePreference
import com.qurafy.hamraj37.ui.home.HomeScreen
import com.qurafy.hamraj37.ui.reader.QuranReaderScreen
import com.qurafy.hamraj37.ui.reader.QuranReaderViewModel
import com.qurafy.hamraj37.ui.theme.QurafyTheme

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
                                onSetNightMode = { mode -> viewModel.setNightMode(mode) },
                                onToggleBookmark = { page -> viewModel.toggleBookmark(page) },
                                onOpenReader = { targetPage ->
                                    viewModel.saveLastReadPage(targetPage)
                                    targetReaderPage = targetPage
                                    currentDestination = ScreenDestination.READER
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
                }
            }
        }
    }
}
