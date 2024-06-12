package com.catscoffeeandkitchen.ui.list

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.WorkoutPlanRepository
import com.catscoffeeandkitchen.models.Workout
import com.catscoffeeandkitchen.models.WorkoutPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutPlansViewModel @Inject constructor(
    private val workoutPlanRepository: WorkoutPlanRepository,
): ViewModel() {
    data class PlansUIState(
        val plans: List<WorkoutPlan>? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    val plans = workoutPlanRepository.getAllPlans()
        .map { PlansUIState(plans = it) }
        .onStart { emit(PlansUIState(isLoading = true)) }
        .catch { emit(PlansUIState(error = it)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            PlansUIState(isLoading = true)
        )

    private var _events: MutableSharedFlow<PlanEvent> = MutableSharedFlow()
    val events: SharedFlow<PlanEvent?> = _events

    sealed class PlanEvent {
        class CreatedPlan(val id: Long): PlanEvent()
    }


    fun createWorkoutPlan() = viewModelScope.launch {
        val id = workoutPlanRepository.createPlan(WorkoutPlan(id = 0L))
        _events.emit(PlanEvent.CreatedPlan(id))
    }
}
