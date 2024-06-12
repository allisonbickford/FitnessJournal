package com.catscoffeeandkitchen.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.data.SettingRepository
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.WeightUnit
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    settingsRepository: SettingRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    data class ExerciseUIState(
        val originalExercise: Exercise? = null,
        val exercise: Exercise? = null,
        val similarExercises: List<Exercise> = emptyList(),
        val stats: ExerciseProgressStats? = null,
        val cues: List<String>? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    ) {
        val isNotOriginal: Boolean
            get() = originalExercise != null
                    && exercise != null
                    && exercise.id != originalExercise.id
    }

    private var _uiState = MutableStateFlow(ExerciseUIState(isLoading = true))
    val uiState: StateFlow<ExerciseUIState> = _uiState

    val unit = settingsRepository.getWeightUnit()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            WeightUnit.Pounds
        )

    init {
        savedStateHandle.get<Long?>("exerciseId")?.let { id ->
            viewModelScope.launch {
                selectExercise(id, original = true)
            }
        }
    }

    fun addAndSelectExercise(exercise: Exercise) {
        viewModelScope.launch {
            val result = exerciseRepository.getOrCreate(exercise)
            selectExercise(result.id)
        }
    }

    private suspend fun selectExercise(id: Long, original: Boolean = false) {
        exerciseRepository.get(id)
            .catch {
                Timber.e(it)
                _uiState.emit(uiState.value.copy(error = it, isLoading = false))
            }
            .onStart {
                _uiState.emit(uiState.value.copy(isLoading = true))
            }
            .collect { exercise ->
                _uiState.emit(uiState.value.copy(
                    exercise = exercise,
                    isLoading = false
                ).let {
                    if (original) {
                        it.copy(originalExercise = exercise)
                    } else {
                        it
                    }
                })
                getSimilarExercises(exercise)
                getExerciseStats(exercise)
                getExerciseCues(exercise)
            }
    }

    private fun getSimilarExercises(exercise: Exercise) = viewModelScope.launch {
        runCatching {
            val exercises = exerciseRepository.getSimilarExercises(exercise)
            _uiState.emit(uiState.value.copy(similarExercises = exercises))
        }.onFailure {
            Timber.e(it)
        }
    }

    private fun getExerciseStats(exercise: Exercise) = viewModelScope.launch {
        runCatching {
            val stats = exerciseRepository.getStats(exercise)
            _uiState.emit(uiState.value.copy(stats = stats))
        }.onFailure {
            Timber.e(it)
        }
    }

    private fun getExerciseCues(exercise: Exercise) = viewModelScope.launch {
        runCatching {
            val cues = exerciseRepository.getExerciseCues(exercise)
            _uiState.emit(uiState.value.copy(cues = cues))
        }.onFailure {
            Timber.e(it)
        }
    }
}