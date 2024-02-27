package com.catscoffeeandkitchen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.models.Exercise
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

@HiltViewModel(assistedFactory = ExerciseDetailViewModel.Factory::class)
class ExerciseDetailViewModel @AssistedInject constructor(
    @Assisted private val exerciseId: Long,
    exerciseRepository: ExerciseRepository,
): ViewModel() {
    data class ExerciseUIState(
        val exercise: Exercise? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    val exercise: StateFlow<ExerciseUIState> = exerciseRepository.get(exerciseId)
        .map { ExerciseUIState(exercise = it) }
        .onStart { emit(ExerciseUIState(isLoading = true)) }
        .catch { emit(ExerciseUIState(error = it)) }
        .onEach { Timber.d("Got Exercise ${it.exercise?.name}") }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ExerciseUIState(isLoading = true)
        )

    @AssistedFactory
    interface Factory {
        fun create(exerciseId: Long): ExerciseDetailViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            exerciseId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(exerciseId) as T
            }
        }
    }
}