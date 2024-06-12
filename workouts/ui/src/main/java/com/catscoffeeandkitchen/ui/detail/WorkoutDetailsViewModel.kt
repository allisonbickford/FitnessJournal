package com.catscoffeeandkitchen.ui.detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.data.SettingRepository
import com.catscoffeeandkitchen.data.WorkoutPlanRepository
import com.catscoffeeandkitchen.data.WorkoutRepository
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.Workout
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.models.WorkoutPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    private val workoutPlanRepository: WorkoutPlanRepository,
    settingsRepository: SettingRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    data class WorkoutUIState(
        val workout: Workout? = null,
        val plan: WorkoutPlan? = null,
        val personalBests: List<WorkoutEntry> = emptyList(),
        val isLoading: Boolean = false,
        val exception: Throwable? = null,
    )

    val uiState: StateFlow<WorkoutUIState>
        = combine(
            workoutRepository.get(savedStateHandle["workoutId"] ?: 0L),
            workoutPlanRepository.getPlanForWorkout(savedStateHandle["workoutId"] ?: 0L),
            workoutRepository.getPersonalBests((savedStateHandle["workoutId"] ?: 0L))
        ) { workout, plan, records ->
            WorkoutUIState(
                workout = workout,
                plan = plan,
                personalBests = records
            )
        }
        .onStart { emit(WorkoutUIState(isLoading = true)) }
        .catch { emit(WorkoutUIState(exception = it)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            WorkoutUIState(isLoading = true)
        )

    val weightUnit: Flow<com.catscoffeeandkitchen.models.WeightUnit> = settingsRepository.getWeightUnit()
    val timers: Flow<List<Long>> = settingsRepository.getTimers()

    var editingGroup = mutableStateOf(null as ExerciseGroup?)
    private var isCollectingSavedState = false

    fun collectSavedState(handle: SavedStateHandle) {
        if (isCollectingSavedState) return

        viewModelScope.launch {
            handle
                .getStateFlow<String?>("exerciseToAdd", initialValue = null)
                .combine(
                    handle.getStateFlow<Int?>("swappingExercise", initialValue = null)
                ) { adding, swapping ->
                    adding to swapping
                }
                .collect { pair ->
                    Timber.d("Saved state updated " +
                            "add = ${pair.first}, " +
                            "swap = ${pair.second}")

                    if (pair.first != null && pair.second != null) {
                        setExercise(pair.second!!, pair.first!!)
                    } else if (pair.first != null) {
                        uiState.value.workout?.let {
                            addEntryWithExerciseName(
                                it,
                                pair.first!!
                            )
                        }
                    }
                }
        }

        isCollectingSavedState = true
    }

    fun setExercise(
        exercisePosition: Int,
        exerciseName: String,
    ) = viewModelScope.launch {
        uiState.value.workout?.let { workoutData ->
            val exercise = exerciseRepository.getOrCreateWithName(exerciseName)
            val entry = workoutData.entries.firstOrNull { it.position == exercisePosition }
            if (entry != null) {
                workoutRepository.updateEntry(
                    workoutData.id,
                    entry.copy(exercise = exercise)
                )
            }
        }
    }

    fun addEntry(workoutId: Long, entry: WorkoutEntry) = viewModelScope.launch {
        workoutRepository.addEntry(
            workoutId,
            entry
        )
    }

    fun addEntryWithExerciseName(wo: Workout, name: String) = viewModelScope.launch {
        val exercise = exerciseRepository.getOrCreateWithName(name)
        workoutRepository.addEntry(
            wo.id,
            WorkoutEntry(
                position = wo.entries.size,
                exercise = exercise
            )
        )
    }

    fun addSet(entry: WorkoutEntry) = viewModelScope.launch {
        if (entry.sets.isEmpty()) {
            workoutRepository.addSetWithExercise(entry.id, entry.exercise)
        } else {
            workoutRepository.addSet(entry.id, entry.sets.last().copy(id = 0L, completedAt = null))
        }
    }

    fun updateWorkout(wo: Workout) = viewModelScope.launch {
        workoutRepository.updateWorkout(wo)
    }

    fun finishWorkout(wo: Workout) = viewModelScope.launch {
        workoutRepository.updateWorkout(wo.copy(completedAt = OffsetDateTime.now()))
    }

    fun createPlanFromWorkout(wo: Workout) = viewModelScope.launch {
        workoutPlanRepository.createPlanFromWorkout(wo)
    }

    fun removeEntry(entry: WorkoutEntry) = viewModelScope.launch {
        workoutRepository.removeEntry(entry)
    }

    fun moveEntryTo(
        workoutId: Long,
        entry: WorkoutEntry,
        newPosition: Int
    ) = viewModelScope.launch {
        workoutRepository.moveEntry(workoutId, entry, newPosition)
    }

    fun updateEntry(workoutId: Long, entry: WorkoutEntry) = viewModelScope.launch {
        workoutRepository.updateEntry(workoutId, entry)
    }

    fun updateSet(entryId: Long, set: ExerciseSet) = viewModelScope.launch {
        workoutRepository.updateSet(entryId, set)
    }

    fun updateSets(entryId: Long, sets: List<ExerciseSet>) = viewModelScope.launch {
        workoutRepository.updateSets(entryId, sets)
    }

    @Suppress("MagicNumber")
    fun addWarmupSets(
        entry: WorkoutEntry,
        unit: com.catscoffeeandkitchen.models.WeightUnit
    ) = viewModelScope.launch {
        workoutRepository.addWarmupSets(entry, unit)
    }

    fun replaceWithGroup(entry: WorkoutEntry): Job = viewModelScope.launch {
        uiState.value.workout?.let { wo ->
            entry.sets.let { sets ->
                workoutRepository.removeSets(sets.map { it.id })
            }

            workoutRepository.updateEntry(
                wo.id,
                entry.copy(
                    exercise = null,
                )
            )
        }
    }

    fun removeSet(id: Long) = viewModelScope.launch {
        workoutRepository.removeSet(id)
    }
}
