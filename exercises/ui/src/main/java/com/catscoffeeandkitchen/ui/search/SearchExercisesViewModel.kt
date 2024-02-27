package com.catscoffeeandkitchen.ui.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
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

    fun searchExercises(search: ExerciseSearch) = viewModelScope.launch {
        _search.emit(search)
    }

    fun createExercise(exercise: Exercise) = viewModelScope.launch {
        exerciseRepository.create(exercise)
    }

    fun updateExercise(exercise: Exercise) = viewModelScope.launch {
        exerciseRepository.update(exercise)
    }
}
