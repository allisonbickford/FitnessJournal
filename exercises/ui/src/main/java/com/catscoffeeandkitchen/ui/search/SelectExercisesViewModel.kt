package com.catscoffeeandkitchen.ui.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.models.Exercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SelectExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _search = MutableSharedFlow<ExerciseSearch>()
    val search: Flow<ExerciseSearch> = _search.distinctUntilChanged()
        .shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            replay = 1
        )

    val pagedExerciseFlow: Flow<PagingData<Exercise>> = search.flatMapLatest { request ->
        exerciseRepository.getSearchExercisesPager(
            request.name.orEmpty(),
            request.muscle.orEmpty(),
            request.category.orEmpty()
        )
    }.cachedIn(viewModelScope)

    private var _creatingExercise: MutableState<Exercise?> = mutableStateOf(null)
    val creatingExercise: State<Exercise?> = _creatingExercise

    private var _selectedExercises: MutableStateFlow<List<Exercise>> = MutableStateFlow(emptyList())
    val selectedExercises: Flow<List<Exercise>> = _selectedExercises

    init {
        val initiallySelected = savedStateHandle.get<String>("selectedExercises")
        Timber.d("*** savedStateHandle had $initiallySelected")
        if (initiallySelected != null) {
            viewModelScope.launch {
//                getExercisesUseCase.run(initiallySelected.split("|")).collect { state ->
//                    if (state is DataState.Success) {
//                        _selectedExercises.emit(state.data)
//                    }
//                }
            }
        }
    }

    fun searchExercises(search: ExerciseSearch) = viewModelScope.launch {
        _search.emit(search)
    }

    fun createExercise(exercise: Exercise) = viewModelScope.launch {
        exerciseRepository.create(exercise)
    }

    fun updateExercise(exercise: Exercise) = viewModelScope.launch {
        exerciseRepository.update(exercise)
    }

    fun selectExercise(exercise: Exercise) = viewModelScope.launch {
        _selectedExercises.emit(_selectedExercises.value.plus(exercise))
    }

    fun unselectExercise(exercise: Exercise) = viewModelScope.launch {
        _selectedExercises.emit(_selectedExercises.value.filterNot { it.name == exercise.name })
    }

}
