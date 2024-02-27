package com.catscoffeeandkitchen.stats.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.data.SettingRepository
import com.catscoffeeandkitchen.data.WorkoutRepository
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    workoutRepository: WorkoutRepository,
    settingsRepository: SettingRepository
) : ViewModel() {
    data class StatsUIState(
        val selectedExercise: Exercise? = null,
        val entries: List<WorkoutEntry>? = null,
        val exercises: List<Exercise>? = null,
        val workoutDates: List<OffsetDateTime>? = null,
        val unit: WeightUnit = WeightUnit.Pounds,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    private var _statsUIState = MutableStateFlow(StatsUIState(isLoading = true))
    val statsUIState: StateFlow<StatsUIState> = _statsUIState

    private val _selectedExercise = MutableSharedFlow<Exercise?>()

    private val _workoutDates = workoutRepository.getCompletedAtDates(6)
        .catch { _statsUIState.emit(statsUIState.value.copy(error = it)) }

    private val _weightUnit = settingsRepository.getWeightUnit()
        .catch { _statsUIState.emit(statsUIState.value.copy(error = it)) }

    init {
        getExercises()

        viewModelScope.launch {
            _weightUnit.collect { unit ->
                _statsUIState.emit(statsUIState.value.copy(unit = unit))
            }
        }

        viewModelScope.launch {
            _workoutDates.collect { dates ->
                _statsUIState.emit(statsUIState.value.copy(workoutDates = dates))
            }
        }

        viewModelScope.launch {
            _workoutDates.collect { dates ->
                _statsUIState.emit(statsUIState.value.copy(workoutDates = dates))
            }
        }

        viewModelScope.launch {
            _selectedExercise.collect { exercise ->
                runCatching {
                    exercise?.let { selected ->
                        val entries = workoutRepository.getSetsForExercise(selected)
                        _statsUIState.emit(statsUIState.value.copy(
                            selectedExercise = selected,
                            entries = entries
                        ))
                    }
                }.onFailure {
                    _statsUIState.emit(statsUIState.value.copy(error = it))
                }
            }
        }
    }

    private fun getExercises() = viewModelScope.launch {
        runCatching {
            val exercises = exerciseRepository.getAll()
            _statsUIState.emit(StatsUIState(exercises = exercises))
        }.onFailure {
            _statsUIState.emit(statsUIState.value.copy(error = it))
        }
    }

    fun selectExercise(exercise: Exercise?)= viewModelScope.launch {
        _statsUIState.emit(statsUIState.value.copy(selectedExercise = exercise))
    }
}
