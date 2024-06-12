package com.catscoffeeandkitchen.ui.groups.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.ExerciseRepository
import com.catscoffeeandkitchen.data.GroupRepository
import com.catscoffeeandkitchen.models.ExerciseGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    data class GroupUIState(
        val group: ExerciseGroup? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    val groupUIState: StateFlow<GroupUIState> = groupRepository
        .get(savedStateHandle["groupId"] ?: 0L)
        .map { gr ->
            if (gr != null) {
                GroupUIState(
                    group = gr
                )
            } else {
                GroupUIState(
                    error = Exception(
                        "Group with ID ${savedStateHandle.get<Long?>("groupId")} not found")
                )
            }
        }
        .onStart { emit(GroupUIState(isLoading = true)) }
        .catch { emit(GroupUIState(error = it)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            GroupUIState(isLoading = true)
        )

    fun updateGroup(group: ExerciseGroup) = viewModelScope.launch {
        runCatching {
            groupRepository.updateGroup(group)
        }.onFailure {
            Timber.e(it)
        }
    }

    fun updateGroupExercises(group: ExerciseGroup, names: List<String>) = viewModelScope.launch {
        runCatching {
            groupRepository.updateExercisesInGroup(group, names)
        }.onFailure {
            Timber.e(it)
        }
    }

    fun addExerciseToGroup(name: String) = viewModelScope.launch {
        runCatching {
            groupUIState.filter { it.group != null }.take(1).collect { result ->
                groupRepository.addExercisesToGroup(
                    result.group!!,
                    listOf(name)
                )
            }
        }.onFailure {
            Timber.e(it)
        }
    }
}