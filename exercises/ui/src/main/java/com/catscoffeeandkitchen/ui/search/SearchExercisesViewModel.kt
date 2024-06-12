package com.catscoffeeandkitchen.ui.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.models.Exercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    data class SearchUIState(
        val search: ExerciseSearch = ExerciseSearch(),
        val exercises: List<Exercise>? = null,
        val page: Int = 1,
        val hasMore: Boolean = true,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    private var _uiState = MutableStateFlow(
        SearchUIState(
            isLoading = true,
            search = ExerciseSearch(
                muscle = savedStateHandle["muscle"],
                category = savedStateHandle["category"]
            )
        )
    )
    val uiState: StateFlow<SearchUIState> = _uiState

    private var searchJob: Job? = null
    private var nextPageJob: Job? = null
    private var moreResultsJob: Job? = null

    fun getNextPage() {
        nextPageJob = viewModelScope.launch {
            val nextPage = uiState.value.page + 1
            val search = uiState.value.search

            _uiState.emit(uiState.value.copy(
                isLoading = true
            ))

            runCatching {
                exerciseRepository.getSearchedExercises(
                    search = search.name.orEmpty(),
                    muscle = search.muscle.orEmpty(),
                    category = search.category.orEmpty(),
                    limit = 50,
                    offset = nextPage * 50
                )
            }.onSuccess { page ->
                _uiState.emit(
                    uiState.value.copy(
                        isLoading = false,
                        exercises = uiState.value.exercises.orEmpty() + page.exercises,
                        hasMore = page.hasMore,
                        page = nextPage
                    )
                )
            }.onFailure {
                Timber.e(it)
                _uiState.emit(
                    uiState.value.copy(
                        isLoading = false,
                        error = it
                    )
                )
            }
        }
    }

    fun searchExercises(search: ExerciseSearch) {
        cancelRunningSearch()

        searchJob = viewModelScope.launch {
            _uiState.emit(SearchUIState(
                isLoading = true,
                search = search
            ))

            runCatching {
                exerciseRepository.getSearchedExercises(
                    search = search.name.orEmpty(),
                    muscle = search.muscle.orEmpty(),
                    category = search.category.orEmpty(),
                    limit = 50,
                    offset = 0
                )
            }.onSuccess { page ->
                _uiState.emit(
                    uiState.value.copy(
                        isLoading = false,
                        exercises = page.exercises,
                        hasMore = page.hasMore
                    )
                )

                if (!search.name.isNullOrBlank() || (page.exercises.size < 50 && page.hasMore)) {
                    moreResultsJob = launch(Dispatchers.IO) {
                        getMoreResults()
                    }
                }
            }.onFailure {
                Timber.e(it)
                _uiState.emit(
                    uiState.value.copy(
                        isLoading = false,
                        error = it
                    )
                )
            }
        }
    }

    /**
     * Get all paged exercises and emit the results after each request.
     */
    private suspend fun getMoreResults() {
        val state = uiState.value
        var page = state.page
        var hasMore = state.hasMore
        val exercises = state.exercises.orEmpty().toMutableList()

        _uiState.emit(
            state.copy(isLoading = true)
        )

        while (hasMore) {
            try {
                val result = exerciseRepository.getSearchedExercises(
                    search = state.search.name.orEmpty(),
                    muscle = state.search.muscle.orEmpty(),
                    category = state.search.category.orEmpty(),
                    limit = 50,
                    offset = page * 50
                )

                exercises.addAll(result.exercises)

                _uiState.emit(
                    state.copy(
                        isLoading = result.hasMore,
                        exercises = exercises,
                        page = page,
                        hasMore = result.hasMore
                    )
                )

                page++
                hasMore = result.hasMore
            } catch (error: Exception) {
                hasMore = false
                _uiState.emit(state.copy(
                    isLoading = false,
                    error = error
                ))
            }
        }

        _uiState.emit(
            uiState.value.copy(isLoading = false)
        )
    }

    /**
     * Stops all jobs retrieving exercises data.
     */
    private fun cancelRunningSearch() {
        searchJob?.cancel("Interrupted")
        moreResultsJob?.cancel("Interrupted")
        nextPageJob?.cancel("Interrupted")
    }

    suspend fun createExercise(exercise: Exercise) {
        exerciseRepository.create(exercise)
    }

    fun updateExercise(exercise: Exercise) = viewModelScope.launch {
        exerciseRepository.update(exercise)
    }
}
