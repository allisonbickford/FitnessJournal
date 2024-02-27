package com.catscoffeeandkitchen.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.WorkoutPlanRepository
import com.catscoffeeandkitchen.data.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewWorkoutViewModel @Inject constructor(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val plans = workoutPlanRepository.getAllPlans()
        .onEach { if (it.isEmpty()) createWorkout() }

    private var _createdWorkout = MutableStateFlow(null as Long?)
    val createdWorkout: StateFlow<Long?> = _createdWorkout

    fun createWorkout(planId: Long? = null) = viewModelScope.launch {
        if (planId != null) {
            _createdWorkout.value = workoutPlanRepository.createWorkoutFromPlan(planId)
        } else {
            _createdWorkout.value = workoutRepository.createWorkout()
        }
    }

    init {
        savedStateHandle.get<Long?>("planId")?.takeIf { it > 0 }.let { planId ->
            createWorkout(planId)
        }
    }
}