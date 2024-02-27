package com.catscoffeeandkitchen.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.catscoffeeandkitchen.data.WorkoutRepository
import com.catscoffeeandkitchen.models.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
): ViewModel() {
    val pagedWorkouts: Flow<PagingData<Workout>> = workoutRepository.getPagedWorkouts()

    fun deleteWorkout(workout: Workout) = viewModelScope.launch {
        workoutRepository.removeWorkout(workout.id)
    }
}
