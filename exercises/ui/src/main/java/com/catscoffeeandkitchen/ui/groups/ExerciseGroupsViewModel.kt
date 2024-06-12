package com.catscoffeeandkitchen.ui.groups

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.GroupRepository
import com.catscoffeeandkitchen.models.ExerciseGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExerciseGroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository
): ViewModel() {
    data class GroupUIState(
        val groups: List<ExerciseGroup>? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    val exerciseGroups: StateFlow<GroupUIState> = groupRepository.getAllGroups()
        .map<List<ExerciseGroup>, GroupUIState> { GroupUIState(groups = it) }
        .onStart { emit(GroupUIState(isLoading = true)) }
        .catch { emit(GroupUIState(error = it)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            GroupUIState(isLoading = true)
        )

    sealed class GroupEvent {
        class UpdatedGroup(val group: ExerciseGroup): GroupEvent()
    }

    private var _events = MutableSharedFlow<GroupEvent>()
    val events: SharedFlow<GroupEvent> = _events.asSharedFlow()

    private var _editingGroup = MutableStateFlow(null as ExerciseGroup?)

    private var isCollectingSavedState = false

    fun createGroup(names: List<String>) = viewModelScope.launch {
        runCatching {
            groupRepository.createGroup("group", names)
        }.onFailure {
            Timber.e("ExerciseGroupsViewModel:createGroup($names)", it)
        }
    }

    fun renameGroup(group: ExerciseGroup, updatedName: String) = viewModelScope.launch {
        runCatching {
            groupRepository.updateGroup(group.copy(name = updatedName))
            _events.emit(GroupEvent.UpdatedGroup(group))
        }.onFailure {
            Timber.e("ExerciseGroupsViewModel:updateGroup($group, $updatedName)", it)
        }
    }

    fun updateGroupExercises(group: ExerciseGroup, exercises: List<String>) = viewModelScope.launch {
        runCatching {
            groupRepository.updateExercisesInGroup(group, exercises)
            _events.emit(GroupEvent.UpdatedGroup(group))
        }.onFailure {
            Timber.e("ExerciseGroupsViewModel:updateGroupExercises($group, $exercises)", it)
        }
    }

    fun removeGroup(group: ExerciseGroup) = viewModelScope.launch {
        runCatching {
            groupRepository.removeGroup(group.id)
        }.onFailure {
            Timber.e("ExerciseGroupsViewModel:removeGroup($group)", it)
        }
    }

    fun setEditingGroup(group: ExerciseGroup) = viewModelScope.launch {
        _editingGroup.emit(group)
    }

    fun collectSavedState(handle: SavedStateHandle) {
        if (isCollectingSavedState) return

        viewModelScope.launch {
            handle
                .getStateFlow<String?>("selectedExercises", initialValue = null)
                .filterNotNull()
                .collect { exercises ->
                    if (_editingGroup.value == null) {
                        createGroup(exercises.split("|"))
                    } else {
                        updateGroupExercises(_editingGroup.value!!, exercises.split("|"))
                    }
                }
        }

        isCollectingSavedState = true
    }
}
