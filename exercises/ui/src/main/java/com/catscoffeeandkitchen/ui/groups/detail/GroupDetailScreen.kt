package com.catscoffeeandkitchen.ui.groups.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.exercises.ui.R
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.ui.theme.Spacing
import kotlinx.coroutines.flow.filter

@Composable
fun GroupDetailScreen(
    addingExercise: String? = null,
    navigateToAddExercise: () -> Unit,
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.groupUIState.collectAsState()

    LaunchedEffect(Unit) {
        snapshotFlow { addingExercise }.filter { it != null }.collect { adding ->
            viewModel.addExerciseToGroup(adding.orEmpty())
        }
    }

    Scaffold {
        GroupDetailScreenContent(
            state = uiState,
            onGroupAction = { action ->
                when (action) {
                    is GroupAction.UpdateGroupExercises -> {
                        viewModel.updateGroupExercises(action.group, action.exerciseNames)
                    }
                    is GroupAction.UpdateGroupName -> {
                        viewModel.updateGroup(action.group)
                    }
                    is GroupAction.RemoveExercise -> {
                        viewModel.updateGroupExercises(
                            action.group,
                            action.group.exercises
                                .filter { ex -> ex.id != action.exercise.id }
                                .map { ex -> ex.name }
                        )
                    }
                    GroupAction.NavigateToAddExercise -> {
                        navigateToAddExercise()

                    }
                }
            },
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
private fun GroupDetailScreenContent(
    state: GroupDetailViewModel.GroupUIState,
    onGroupAction: (GroupAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    state.error?.let {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(it.localizedMessage
                ?: stringResource(id = R.string.group_error_message))
        }
    }

    state.group?.let { group ->
        GroupContent(
            group = group,
            onGroupAction = onGroupAction,
            modifier = modifier
        )
    }

}

@Composable
private fun GroupContent(
    group: ExerciseGroup,
    onGroupAction: (GroupAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingName by remember { mutableStateOf(false) }
    var updatedName by remember { mutableStateOf(group.name.orEmpty()) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Crossfade(
                isEditingName,
                label = "Update Group Name Animation"
            ) { isEditing ->
                if (isEditing) {
                    OutlinedTextField(
                        value = updatedName,
                        onValueChange = { updatedName = it },
                        label = { Text(stringResource(id = R.string.group_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                onGroupAction(
                                    GroupAction.UpdateGroupName(group.copy(name = updatedName))
                                )
                                isEditingName = false
                            }) {
                                Icon(Icons.Default.Check, "Done editing")
                            }
                        }
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Default)
                    ) {
                        Text(
                            group.name ?: stringResource(R.string.group_x, group.id),
                            style = MaterialTheme.typography.headlineSmall
                        )

                        IconButton(onClick = { isEditingName = true }) {
                            Icon(Icons.Default.Edit, "Edit group name")
                        }
                    }
                }
            }
        }

        itemsIndexed(group.exercises) { index, exercise ->
            if (index == 0) {
                HorizontalDivider()
            }

            GroupExerciseItem(
                exercise = exercise,
                onRemoveExercise = {
                    onGroupAction(GroupAction.RemoveExercise(group, exercise))
                }
            )

            HorizontalDivider()
        }

        item {
            TextButton(
                onClick = { onGroupAction(GroupAction.NavigateToAddExercise) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.add_exercise))
            }
        }
    }
}

@Composable
private fun GroupExerciseItem(
    exercise: Exercise,
    onRemoveExercise: () -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Spacing.Default,
                    vertical = Spacing.Half
                )
        ) {
            exercise.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Depiction of ${exercise.name}",
                    modifier = Modifier
                        .size(72.dp)
                        .padding(end = Spacing.Default)
                )
            }

            Column {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    exercise.musclesWorked.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.weight(1f))

            IconButton(onClick = onRemoveExercise) {
                Icon(Icons.Default.Delete, "Remove Exercise")
            }
        }
    }
}
