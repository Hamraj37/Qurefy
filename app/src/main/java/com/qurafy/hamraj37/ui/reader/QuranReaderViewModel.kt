package com.qurafy.hamraj37.ui.reader

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.qurafy.hamraj37.audio.QuranAudioPlayerManager
import com.qurafy.hamraj37.data.model.PdfDrawingStroke
import com.qurafy.hamraj37.data.model.QuranMetaData
import com.qurafy.hamraj37.data.model.QuranVerse
import com.qurafy.hamraj37.data.model.Reciter
import com.qurafy.hamraj37.data.model.Surah
import com.qurafy.hamraj37.data.model.UpdateInfo
import com.qurafy.hamraj37.data.model.VerseMatch
import com.qurafy.hamraj37.data.repository.GitHubUpdateChecker
import com.qurafy.hamraj37.data.repository.NightModePreference
import com.qurafy.hamraj37.data.repository.QuranTextRepository
import com.qurafy.hamraj37.data.repository.UserPreferencesRepository
import com.qurafy.hamraj37.pdf.QuranPdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuranReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)
    private val quranTextRepository = QuranTextRepository()
    private val quranPdfRepository = QuranPdfRepository(application)
    val audioPlayerManager = QuranAudioPlayerManager.getInstance(application)

    val lastReadPage: StateFlow<Int> = repository.lastReadPage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    val nightMode: StateFlow<NightModePreference> = repository.nightModePreference.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NightModePreference.SYSTEM
    )

    val bookmarkedPages: StateFlow<Set<Int>> = repository.bookmarkedPages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val fontSizeSp: StateFlow<Float> = repository.textSizeSp.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 16f
    )

    // PDF Annotation & Drawing State
    private val _isDrawingMode = MutableStateFlow(false)
    val isDrawingMode: StateFlow<Boolean> = _isDrawingMode.asStateFlow()

    private val _activeBrushColor = MutableStateFlow(Color(0xFFFFD700))
    val activeBrushColor: StateFlow<Color> = _activeBrushColor.asStateFlow()

    private val _isHighlighter = MutableStateFlow(true)
    val isHighlighter: StateFlow<Boolean> = _isHighlighter.asStateFlow()

    val pageAnnotations: StateFlow<Map<Int, List<PdfDrawingStroke>>> = repository.pageAnnotations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    // Audio Player State
    val playingSurah: StateFlow<Surah> = audioPlayerManager.currentSurah
    val currentReciter: StateFlow<Reciter> = audioPlayerManager.currentReciter
    val isAudioPlaying: StateFlow<Boolean> = audioPlayerManager.isPlaying
    val isAudioBuffering: StateFlow<Boolean> = audioPlayerManager.isBuffering
    val audioPositionMs: StateFlow<Long> = audioPlayerManager.currentPositionMs
    val audioDurationMs: StateFlow<Long> = audioPlayerManager.durationMs
    val playbackSpeed: StateFlow<Float> = audioPlayerManager.playbackSpeed
    val isAudioPlayerBarVisible: StateFlow<Boolean> = audioPlayerManager.isPlayerVisible

    private val _isAudioSheetOpen = MutableStateFlow(false)
    val isAudioSheetOpen: StateFlow<Boolean> = _isAudioSheetOpen.asStateFlow()

    private val _pageCount = MutableStateFlow(604)
    val pageCount: StateFlow<Int> = _pageCount.asStateFlow()

    private val _isNavigationSheetOpen = MutableStateFlow(false)
    val isNavigationSheetOpen: StateFlow<Boolean> = _isNavigationSheetOpen.asStateFlow()

    private val _isJumpToPageDialogOpen = MutableStateFlow(false)
    val isJumpToPageDialogOpen: StateFlow<Boolean> = _isJumpToPageDialogOpen.asStateFlow()

    // Quran Text Repository & Search State
    private val _textSearchQuery = MutableStateFlow("")
    val textSearchQuery: StateFlow<String> = _textSearchQuery.asStateFlow()

    private val _textSearchResults = MutableStateFlow<List<VerseMatch>>(emptyList())
    val textSearchResults: StateFlow<List<VerseMatch>> = _textSearchResults.asStateFlow()

    private var searchJob: Job? = null

    fun onTextQueryChanged(query: String) {
        _textSearchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            val results = quranTextRepository.searchVerses(query)
            _textSearchResults.value = results
        }
    }

    fun getVersesForPage(page: Int): List<QuranVerse> {
        return quranTextRepository.getVersesForPage(page)
    }

    fun increaseFontSize() {
        viewModelScope.launch {
            val current = fontSizeSp.value
            repository.setTextSizeSp((current + 2f).coerceAtMost(32f))
        }
    }

    fun decreaseFontSize() {
        viewModelScope.launch {
            val current = fontSizeSp.value
            repository.setTextSizeSp((current - 2f).coerceAtLeast(12f))
        }
    }

    // GitHub Update Checker State
    val currentAppVersionName: String
        get() {
            return try {
                val pInfo = getApplication<Application>().packageManager.getPackageInfo(getApplication<Application>().packageName, 0)
                pInfo.versionName ?: "1.0.1"
            } catch (e: Exception) {
                "1.0.1"
            }
        }

    private val updateChecker = GitHubUpdateChecker()

    private val _availableUpdate = MutableStateFlow<UpdateInfo?>(null)
    val availableUpdate: StateFlow<UpdateInfo?> = _availableUpdate.asStateFlow()

    private val _isCheckingForUpdates = MutableStateFlow(false)
    val isCheckingForUpdates: StateFlow<Boolean> = _isCheckingForUpdates.asStateFlow()

    private val _updateCheckMessage = MutableStateFlow<String?>(null)
    val updateCheckMessage: StateFlow<String?> = _updateCheckMessage.asStateFlow()

    fun checkForUpdates(isManual: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _isCheckingForUpdates.value = true
            val version = currentAppVersionName
            val updateInfo = updateChecker.checkForUpdate(version)
            _isCheckingForUpdates.value = false

            if (updateInfo != null) {
                _availableUpdate.value = updateInfo
            } else if (isManual) {
                _updateCheckMessage.value = "You are using the latest version (v$version)."
            }
        }
    }

    fun dismissUpdateDialog() {
        _availableUpdate.value = null
    }

    fun dismissUpdateMessage() {
        _updateCheckMessage.value = null
    }

    init {
        loadSavedReciter()
        initializePdfRepository()
        checkForUpdates()
    }

    private fun loadSavedReciter() {
        viewModelScope.launch {
            val savedReciterId = repository.selectedReciterId.first()
            val reciter = Reciter.getById(savedReciterId)
            audioPlayerManager.setReciter(reciter)
        }
    }

    private fun initializePdfRepository() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = quranPdfRepository.getPageCount()
            if (count > 0) {
                _pageCount.value = count
            }
        }
    }

    suspend fun renderPdfPage(
        pageIndex: Int,
        width: Int,
        height: Int,
        isNight: Boolean
    ): Bitmap? {
        return quranPdfRepository.renderPage(pageIndex, width, height, isNight)
    }

    fun setDrawingMode(enabled: Boolean) {
        _isDrawingMode.value = enabled
    }

    fun toggleDrawingMode() {
        _isDrawingMode.value = !_isDrawingMode.value
    }

    fun setBrush(color: Color, isHighlighter: Boolean) {
        _activeBrushColor.value = color
        _isHighlighter.value = isHighlighter
    }

    fun addStroke(pageIndex: Int, stroke: PdfDrawingStroke) {
        viewModelScope.launch {
            val currentMap = pageAnnotations.value.toMutableMap()
            val pageStrokes = currentMap[pageIndex]?.toMutableList() ?: mutableListOf()
            pageStrokes.add(stroke)
            currentMap[pageIndex] = pageStrokes
            repository.savePageAnnotations(currentMap)
        }
    }

    fun undoStroke(pageIndex: Int) {
        viewModelScope.launch {
            val currentMap = pageAnnotations.value.toMutableMap()
            val pageStrokes = currentMap[pageIndex]?.toMutableList() ?: return@launch
            if (pageStrokes.isNotEmpty()) {
                pageStrokes.removeAt(pageStrokes.size - 1)
                if (pageStrokes.isEmpty()) {
                    currentMap.remove(pageIndex)
                } else {
                    currentMap[pageIndex] = pageStrokes
                }
                repository.savePageAnnotations(currentMap)
            }
        }
    }

    fun clearPageStrokes(pageIndex: Int) {
        viewModelScope.launch {
            val currentMap = pageAnnotations.value.toMutableMap()
            if (currentMap.containsKey(pageIndex)) {
                currentMap.remove(pageIndex)
                repository.savePageAnnotations(currentMap)
            }
        }
    }

    override fun onCleared() {
        quranPdfRepository.close()
    }

    fun saveLastReadPage(page: Int) {
        viewModelScope.launch {
            repository.setLastReadPage(page)
        }
    }

    fun setNightMode(mode: NightModePreference) {
        viewModelScope.launch {
            repository.setNightModePreference(mode)
        }
    }

    fun toggleBookmark(page: Int) {
        viewModelScope.launch {
            repository.toggleBookmark(page)
        }
    }

    fun setNavigationSheetOpen(isOpen: Boolean) {
        _isNavigationSheetOpen.value = isOpen
    }

    fun setJumpToPageDialogOpen(isOpen: Boolean) {
        _isJumpToPageDialogOpen.value = isOpen
    }

    fun setAudioSheetOpen(isOpen: Boolean) {
        _isAudioSheetOpen.value = isOpen
    }

    // Audio player triggers
    fun playSurah(surah: Surah) {
        audioPlayerManager.playSurah(surah)
    }

    fun playPageSurah(page: Int) {
        val surah = QuranMetaData.getSurahForPage(page) ?: QuranMetaData.surahs.first()
        playSurah(surah)
    }

    fun toggleAudioPlayPause() {
        audioPlayerManager.togglePlayPause()
    }

    fun nextSurahAudio() {
        audioPlayerManager.nextSurah()
    }

    fun previousSurahAudio() {
        audioPlayerManager.previousSurah()
    }

    fun seekAudioTo(positionMs: Long) {
        audioPlayerManager.seekTo(positionMs)
    }

    fun selectReciter(reciter: Reciter) {
        viewModelScope.launch {
            repository.setSelectedReciterId(reciter.id)
            audioPlayerManager.setReciter(reciter)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        audioPlayerManager.setPlaybackSpeed(speed)
    }

    fun closeAudioPlayer() {
        audioPlayerManager.pause()
        audioPlayerManager.hidePlayerBar()
        _isAudioSheetOpen.value = false
    }
}
