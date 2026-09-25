package com.qurafy.hamraj37.audio

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.PlaybackParameters
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.qurafy.hamraj37.data.model.QuranMetaData
import com.qurafy.hamraj37.data.model.Reciter
import com.qurafy.hamraj37.data.model.Surah
import com.qurafy.hamraj37.service.QuranAudioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class QuranAudioPlayerManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _currentSurah = MutableStateFlow(QuranMetaData.surahs.first())
    val currentSurah: StateFlow<Surah> = _currentSurah.asStateFlow()

    private val _currentReciter = MutableStateFlow(Reciter.DEFAULT)
    val currentReciter: StateFlow<Reciter> = _currentReciter.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _isPlayerVisible = MutableStateFlow(false)
    val isPlayerVisible: StateFlow<Boolean> = _isPlayerVisible.asStateFlow()

    private var progressUpdateJob: Job? = null

    init {
        initializeMediaController()
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(
            context.applicationContext,
            ComponentName(context.applicationContext, QuranAudioService::class.java)
        )
        controllerFuture = MediaController.Builder(context.applicationContext, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                try {
                    val controller = controllerFuture?.get()
                    mediaController = controller
                    setupPlayerListener(controller)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun setupPlayerListener(controller: MediaController?) {
        controller?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                _isPlaying.value = isPlayingNow
                if (isPlayingNow) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = (playbackState == Player.STATE_BUFFERING)
                if (playbackState == Player.STATE_READY) {
                    _durationMs.value = controller.duration.coerceAtLeast(0L)
                } else if (playbackState == Player.STATE_ENDED) {
                    _isPlaying.value = false
                    _currentPositionMs.value = _durationMs.value
                    // Auto-advance to next Surah if available
                    nextSurah()
                }
            }
        })
    }

    fun playSurah(surah: Surah, reciter: Reciter = _currentReciter.value) {
        _currentSurah.value = surah
        _currentReciter.value = reciter
        _isPlayerVisible.value = true

        val audioUrl = reciter.getSurahAudioUrl(surah.number)

        val metadata = MediaMetadata.Builder()
            .setTitle("سُورَةُ ${surah.nameArabic} • ${surah.nameTransliteration}")
            .setArtist(reciter.nameEnglish)
            .setAlbumTitle("Surah ${surah.number} (${surah.nameEnglish})")
            .setDisplayTitle("Surah ${surah.number}: ${surah.nameTransliteration}")
            .setSubtitle(reciter.nameArabic)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(audioUrl))
            .setMediaId("surah_${surah.number}_${reciter.id}")
            .setMediaMetadata(metadata)
            .build()

        mediaController?.let { controller ->
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }
    }

    fun togglePlayPause() {
        mediaController?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                if (controller.playbackState == Player.STATE_ENDED) {
                    controller.seekTo(0)
                }
                controller.play()
            }
        } ?: run {
            // Controller not connected yet, play surah
            playSurah(_currentSurah.value, _currentReciter.value)
        }
    }

    fun pause() {
        mediaController?.pause()
    }

    fun play() {
        mediaController?.play()
    }

    fun seekTo(positionMs: Long) {
        _currentPositionMs.value = positionMs
        mediaController?.seekTo(positionMs)
    }

    fun nextSurah() {
        val nextNumber = _currentSurah.value.number + 1
        if (nextNumber <= 114) {
            val nextSurahObj = QuranMetaData.getSurahByNumber(nextNumber)
            if (nextSurahObj != null) {
                playSurah(nextSurahObj, _currentReciter.value)
            }
        }
    }

    fun previousSurah() {
        val prevNumber = _currentSurah.value.number - 1
        if (prevNumber >= 1) {
            val prevSurahObj = QuranMetaData.getSurahByNumber(prevNumber)
            if (prevSurahObj != null) {
                playSurah(prevSurahObj, _currentReciter.value)
            }
        }
    }

    fun setReciter(reciter: Reciter) {
        if (_currentReciter.value.id != reciter.id) {
            _currentReciter.value = reciter
            val wasPlaying = _isPlaying.value
            val currentPos = _currentPositionMs.value
            playSurah(_currentSurah.value, reciter)
            if (!wasPlaying) {
                mediaController?.pause()
            } else {
                mediaController?.seekTo(currentPos)
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        mediaController?.playbackParameters = PlaybackParameters(speed)
    }

    fun showPlayerBar() {
        _isPlayerVisible.value = true
    }

    fun hidePlayerBar() {
        _isPlayerVisible.value = false
    }

    fun togglePlayerVisibility() {
        _isPlayerVisible.value = !_isPlayerVisible.value
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressUpdateJob = scope.launch {
            while (isActive) {
                mediaController?.let { controller ->
                    _currentPositionMs.value = controller.currentPosition.coerceAtLeast(0L)
                    _durationMs.value = controller.duration.coerceAtLeast(0L)
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    companion object {
        @Volatile
        private var instance: QuranAudioPlayerManager? = null

        fun getInstance(context: Context): QuranAudioPlayerManager {
            return instance ?: synchronized(this) {
                instance ?: QuranAudioPlayerManager(context.applicationContext).also { instance = it }
            }
        }

        fun formatTimeMs(ms: Long): String {
            val totalSeconds = (ms / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}
