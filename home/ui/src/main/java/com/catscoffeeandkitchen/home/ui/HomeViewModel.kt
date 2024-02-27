package com.catscoffeeandkitchen.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.data.SettingRepository
import com.catscoffeeandkitchen.data.WorkoutPlanRepository
import com.catscoffeeandkitchen.data.WorkoutRepository
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.models.WorkoutPlan
import com.catscoffeeandkitchen.models.WorkoutWeekStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val exerciseRepository: ExerciseRepository,
    settingsRepository: SettingRepository
): ViewModel() {

    data class HomeUIState(
        val nextPlan: ResultState<WorkoutPlan> = ResultState(),
        val lastExercises: ResultState<List<WorkoutEntry>> = ResultState(),
        val weekStats: ResultState<WorkoutWeekStats> = ResultState(),
        val mostImprovedExercise: ResultState<ExerciseProgressStats> = ResultState(),
    )

    data class ResultState<T>(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val result: T? = null
    )

    private var _uiState: MutableStateFlow<HomeUIState> = MutableStateFlow(
        HomeUIState(
            nextPlan = ResultState(isLoading = true),
            lastExercises = ResultState(isLoading = true),
            weekStats = ResultState(isLoading = true),
            mostImprovedExercise = ResultState(isLoading = true)
        )
    )
    val uiState: StateFlow<HomeUIState> = _uiState

    val weightUnit: Flow<WeightUnit> = settingsRepository.getWeightUnit()

    init {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                nextPlan = ResultState(isLoading = true)
            )

            runCatching {
                workoutPlanRepository.nextPlanThisWeek().collect { plan ->
                    _uiState.value = uiState.value.copy(
                        nextPlan = ResultState(result = plan)
                    )
                }
            }.onFailure {
                Timber.e(it)
                _uiState.value = uiState.value.copy(
                    nextPlan = ResultState(error = it)
                )
            }
        }

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                lastExercises = ResultState(isLoading = true)
            )

            runCatching {
                workoutRepository.mostRecentEntries(3).collect { plan ->
                    _uiState.value = uiState.value.copy(
                        lastExercises = ResultState(result = plan)
                    )
                }
            }.onFailure {
                Timber.e(it)
                _uiState.value = uiState.value.copy(
                    lastExercises = ResultState(error = it)
                )
            }
        }

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                weekStats = ResultState(isLoading = true)
            )

            runCatching {
                workoutRepository.getWeekStats().collect { plan ->
                    _uiState.value = uiState.value.copy(
                        weekStats = ResultState(result = plan)
                    )
                }
            }.onFailure {
                Timber.e(it)
                _uiState.value = uiState.value.copy(
                    weekStats = ResultState(error = it)
                )
            }
        }

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                mostImprovedExercise = ResultState(isLoading = true)
            )

            runCatching {
                val mostImproved = exerciseRepository.getMostImprovedExercise(12)
                _uiState.value = uiState.value.copy(
                    mostImprovedExercise = ResultState(result = mostImproved)
                )
            }.onFailure {
                Timber.e(it)
                _uiState.value = uiState.value.copy(
                    mostImprovedExercise = ResultState(error = it)
                )
            }
        }
    }
}
