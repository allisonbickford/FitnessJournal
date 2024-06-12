package com.catscoffeeandkitchen.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.data.GroupRepository
import com.catscoffeeandkitchen.data.WorkoutPlanRepository
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.WorkoutPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WorkoutPlanDetailViewModel @Inject constructor(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val groupRepository: GroupRepository,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    data class PlanUIState(
        val plan: WorkoutPlan? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    val planUiState: StateFlow<PlanUIState> = savedStateHandle
        .getStateFlow<Long?>("workoutId", initialValue = null)
        .filterNotNull()
        .flatMapMerge { id ->
            workoutPlanRepository.getPlan(id)
        }
        .map { PlanUIState(it) }
        .onStart { emit(PlanUIState(isLoading = true)) }
        .catch { emit(PlanUIState(error = it)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            PlanUIState(isLoading = true)
        )

    private var _showExerciseGroupNameDialog = MutableStateFlow(false)
    val showExerciseGroupNameDialog = _showExerciseGroupNameDialog

    private var _exercisesToGroup = MutableStateFlow(emptyList<String>())
    var exercisesToGroup = _exercisesToGroup

    private var goalToUpdate: Goal? = null
    private var isCollectingSavedState = false

    fun collectSavedState(handle: SavedStateHandle) {
        if (isCollectingSavedState) return

        viewModelScope.launch {
            handle
                .getStateFlow<String?>("exerciseToAdd", initialValue = null)
                .collect { name ->
                    Timber.d("Saved state updated exerciseToAdd = $name")
                    if (name != null) addExercise(name)
                }
        }

        viewModelScope.launch {
            handle
                .getStateFlow<String?>("selectedExercises", initialValue = null)
                .collect { result ->
                    Timber.d("Saved state updated selectedExercises = $result")
                    if (result != null) {
                        showGroupNameDialog(result.split("|"))
                    }
                }
        }

        viewModelScope.launch {
            handle
                .getStateFlow<Long?>("selectedGroup", initialValue = null)
                .collect { result ->
                    Timber.d("Saved state updated selectedGroup = $result")
                    if (result != null) {
                        selectGroup(result)
                    }
                }
        }

        viewModelScope.launch {
            handle
                .getStateFlow<Long?>("openExerciseId", initialValue = null)
                .collect { exerciseId ->
                    Timber.d("Saved state updated openExerciseId = $exerciseId, goalToUpdate = ${goalToUpdate?.position}")
                    if (exerciseId != null && goalToUpdate != null) {
                        runCatching {
                            val exercise = exerciseRepository.get(exerciseId).first()
                            updateGoal(goalToUpdate!!.copy(exercise = exercise))
                        }.onFailure {
                            Timber.e(it)
                        }
                    }
                }
        }

        isCollectingSavedState = true
    }

    fun waitForGoalUpdate(goal: Goal) {
        goalToUpdate = goal
    }

    fun updateGoal(goal: Goal) = viewModelScope.launch {
        planUiState.value.plan?.let { workout ->
            workoutPlanRepository.updateGoal(
                workout.id,
                goal
            )
        }
    }

    fun repositionGoal(goal: Goal, position: Int) = viewModelScope.launch {
        planUiState.value.plan?.let { workout ->
            workout.goals.firstOrNull { it.position == position }?.let { original ->
                workoutPlanRepository.updateGoal(
                    workout.id,
                    original.copy(position = goal.position)
                )
            }
            workoutPlanRepository.updateGoal(
                workout.id,
                goal
            )
        }
    }

    fun removeGoal(id: Long) = viewModelScope.launch {
        workoutPlanRepository.removeGoal(id)
    }

    private fun addExercise(name: String) = viewModelScope.launch {
        planUiState.value.plan?.let { workout ->
            val exercise = exerciseRepository.getOrCreateWithName(name)
            Timber.d("Attempting to add exercise $exercise")
            workoutPlanRepository.addGoal(
                workout.id,
                Goal(
                    exercise = exercise,
                    position = workout.goals.size
                )
            )
        }
    }

    fun addExerciseGroup(groupName: String, names: List<String>) = viewModelScope.launch {
        planUiState.value.plan?.let { workout ->
            val group = groupRepository.createGroup(groupName, names)
            workoutPlanRepository.addGoal(
                id = workout.id,
                goal = Goal(
                    group = group,
                    position = workout.goals.size
                )
            )
        }
    }

    fun updateWorkoutName(name: String) = viewModelScope.launch {
        planUiState.value.plan?.let { workout ->
            workoutPlanRepository.updatePlan(workout.copy(name = name))
        }
    }

    fun updateWorkoutNotes(note: String) = viewModelScope.launch {
        planUiState.value.plan?.let { workout ->
            workoutPlanRepository.updatePlan(workout.copy(note = note))
        }
    }

    fun updateWeekdays(weekdays: List<DayOfWeek>) = viewModelScope.launch {
        planUiState.value.plan?.let { workout ->
            workoutPlanRepository.updatePlan(workout.copy(daysOfWeek = weekdays))
        }
    }

    fun createWorkoutFromPlan() = viewModelScope.launch  {
        planUiState.value.plan?.let {
            workoutPlanRepository.createWorkoutFromPlan(it.id)
        }
    }

    private fun selectGroup(id: Long) = viewModelScope.launch {
        planUiState.value.plan?.let { workout ->
            workoutPlanRepository.addGoal(
                workout.id,
                Goal(
                    position = workout.goals.size,
                    group = ExerciseGroup(id = id, name = "", emptyList())
                )
            )
        }
    }

    private fun showGroupNameDialog(names: List<String>) {
        _showExerciseGroupNameDialog.value = true
        _exercisesToGroup.value = names
    }

    fun hideGroupNameDialog() {
        _showExerciseGroupNameDialog.value = false
    }
}
