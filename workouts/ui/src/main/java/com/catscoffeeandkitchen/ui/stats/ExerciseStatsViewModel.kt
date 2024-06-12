package com.catscoffeeandkitchen.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.data.SettingRepository
import com.catscoffeeandkitchen.data.WorkoutRepository
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.WorkoutEntry
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ExerciseStatsViewModel.Factory::class)
class ExerciseStatsViewModel @AssistedInject constructor(
    @Assisted val exerciseId: Long,
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    settingsRepository: SettingRepository
): ViewModel() {

    data class UIState(
        val exercise: Exercise? = null,
        val entries: List<WorkoutEntry> = emptyList(),
        val similarExercises: List<Exercise> = emptyList(),
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    val weightUnit = settingsRepository.getWeightUnit()

    private var _uiState = MutableStateFlow(UIState(isLoading = true))
    val uiState: StateFlow<UIState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                exerciseRepository.get(exerciseId),
                flow {
                    emit(workoutRepository.getEntriesForExercise(exerciseId))
                }
            ) { exercise, entries ->
                exercise to entries
            }
            .onStart { _uiState.emit(uiState.value.copy(isLoading = true)) }
            .catch { _uiState.emit(uiState.value.copy(isLoading = false, error = it)) }
            .collect { pair ->
                _uiState.emit(uiState.value.copy(
                    isLoading = false,
                    exercise = pair.first,
                    entries = pair.second
                ))

                getSimilarExercises(pair.first)
            }
        }
    }

    suspend fun getSimilarExercises(exercise: Exercise) {
        val similar = exerciseRepository.getSimilarExercises(exercise)
        _uiState.emit(uiState.value.copy(similarExercises = similar))
    }

    @AssistedFactory
    interface Factory {
        fun create(exerciseId: Long): ExerciseStatsViewModel
    }
}