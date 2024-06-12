package com.catscoffeeandkitchen.exercises.youtube_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.exercises.youtube_player.data.YouTubeRepository
import com.catscoffeeandkitchen.exercises.youtube_player.models.YouTubeVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class YouTubeSearchViewModel @Inject constructor(
    youTubeRepository: YouTubeRepository
): ViewModel() {
    data class VideoUIState(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val videos: List<YouTubeVideo>? = null
    )

    private var _videos = MutableStateFlow(VideoUIState(isLoading = true))
    val videos: StateFlow<VideoUIState> = _videos

    private var _search = MutableSharedFlow<String?>()

    init {
        viewModelScope.launch {
            _search.filterNotNull().collect { query ->
                _videos.emit(videos.value.copy(isLoading = true))
                runCatching {
                    Timber.d("Searching youtube for $query")
                    youTubeRepository.searchYouTubeVideos(query)
                }.onFailure { error ->
                    Timber.e(error)
                    _videos.emit(videos.value.copy(isLoading = false, error = error))
                }.onSuccess { items ->
                    _videos.emit(VideoUIState(videos = items))
                }
            }
        }
    }

    fun search(query: String) = viewModelScope.launch {
        _search.emit(query)
    }
}