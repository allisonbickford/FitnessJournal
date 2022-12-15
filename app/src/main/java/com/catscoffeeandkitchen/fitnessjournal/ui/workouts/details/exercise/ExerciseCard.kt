package com.catscoffeeandkitchen.fitnessjournal.ui.workouts.details.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.fitnessjournal.R
import com.catscoffeeandkitchen.fitnessjournal.ui.components.FitnessJournalCard
import timber.log.Timber
import java.time.OffsetDateTime

enum class ExerciseCardState {
    Group,
    Exercise,
    FinishedExercise,
    Invalid
}

@Composable
fun ExerciseCard(
    uiData: ExerciseUiData,
    uiActions: ExerciseUiActions?,
    navigableActions: ExerciseNavigableActions?,
    onCompleteSet: (OffsetDateTime?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingExercise by remember { mutableStateOf(null as String?) }

    val individualSets = uiData.sets.sortedBy { it.setNumber }
    val cardState = when {
        uiData.exercise != null &&
                (editingExercise == uiData.exercise?.name || individualSets.any { !it.isComplete })
        -> ExerciseCardState.Exercise
        uiData.exercise != null -> ExerciseCardState.FinishedExercise
        uiData.group != null -> ExerciseCardState.Group
        else -> ExerciseCardState.Invalid
    }
//    var cardState by remember { mutableStateOf(initialState) }

    FitnessJournalCard(
        modifier = modifier.padding(horizontal = 8.dp),
        columnItemSpacing = 0.dp,
    ) {
        when (cardState) {
            ExerciseCardState.Group -> {
                CardHeaderRow(title = uiData.group!!.name.ifEmpty { "Select from group" }) { onDismiss ->
                    DropdownMenuItem(
                        text = { Text("edit group") },
                        onClick = {
                            onDismiss()
                            navigableActions?.editGroup(uiData.group)
                        })
                }

                Column(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiData.group.exercises.forEach { exercise ->
                        GroupExerciseItem(exercise, onSelect = { selected ->
                            uiActions?.selectExerciseFromGroup(
                                uiData.group, selected, uiData.position, uiData.expectedSet)
                            uiData.exercise = selected
                        })
                    }
                }
            }
            ExerciseCardState.Exercise -> {
                CardHeaderRow(title = uiData.exercise?.name.orEmpty()) { dismissMenu ->
                    exerciseMenuItems(uiData, uiActions, navigableActions, dismissMenu)
                }

                editableExerciseCardContent(
                    uiData,
                    uiActions,
                    onCompleteExercise = { time ->
                        onCompleteSet(time)
                    }
                )
            }
            ExerciseCardState.FinishedExercise -> {
                CardHeaderRow(title = uiData.exercise?.name.orEmpty()) { dismissMenu ->
                    DropdownMenuItem(
                        text = { Text("edit") },
                        onClick = {
                            editingExercise = uiData.exercise?.name
                            dismissMenu()
                        })
                }

                readOnlyExerciseCardContent(uiData)
            }
            ExerciseCardState.Invalid -> {
                Box() {}
            }
        }
    }
}

@Composable
fun ColumnScope.exerciseMenuItems(
    uiData: ExerciseUiData,
    uiActions: ExerciseUiActions?,
    navigableActions: ExerciseNavigableActions?,
    dismissMenu: () -> Unit
) {
    DropdownMenuItem(
        leadingIcon = { Icon(painterResource(R.drawable.fireplace), "create warm up sets") },
        text = { Text("add pyramid warmup") },
        onClick = {
            uiActions?.addWarmupSets(
                uiData.workoutAddedAt,
                uiData.exercise!!,
                uiData.sets,
                uiData.unit
            )
            dismissMenu()
        })
    DropdownMenuItem(
        leadingIcon = { Icon(Icons.Default.Delete, "remove exercise") },
        text = { Text("remove") },
        onClick = {
            uiActions?.removeExercise(uiData.exercise!!)
            dismissMenu()
        })

    DropdownMenuItem(
        leadingIcon = { Icon(Icons.Default.Refresh, "swap exercise") },
        text = { Text("swap") },
        onClick = {
            navigableActions?.swapExercise(uiData.exercise!!)
            dismissMenu()
        })

    if (uiData.wasChosenFromGroup) {
        DropdownMenuItem(
            leadingIcon = { Icon(painterResource(R.drawable.checklist), "show group") },
            text = { Text("choose from group") },
            onClick = {
                Timber.d("*** choosing from group where exercise was ${uiData.exercise?.name} at ${uiData.position}")
                uiActions?.replaceWithGroup(uiData.position, uiData.exercise!!)
                dismissMenu()
            })
    }

    if (!uiData.isFirstExercise) {
        DropdownMenuItem(
            leadingIcon = { Icon(Icons.Default.KeyboardArrowUp, "move up") },
            text = { Text("move up") },
            onClick = {
                uiActions?.moveExerciseTo(uiData.exercise!!, uiData.position - 1)
                dismissMenu()
            })
    }

    if (!uiData.isLastExercise) {
        DropdownMenuItem(
            leadingIcon = { Icon(Icons.Default.KeyboardArrowDown, "move down") },
            text = { Text("move down") },
            onClick = {
                uiActions?.moveExerciseTo(uiData.exercise!!, uiData.position + 1)
                dismissMenu()
            })
    }
}

@Composable
fun CardHeaderRow(
    title: String,
    dropdownItems: @Composable() ColumnScope.(dismissMenu: () -> Unit) -> Unit,
) {
    var showExtrasDropdown by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title,
            style = MaterialTheme.typography.titleMedium)

        Box(modifier = Modifier.weight(1f)) {
            IconButton(
                onClick = { showExtrasDropdown = !showExtrasDropdown },
            ) {
                Icon(Icons.Default.MoreVert, "more exercise options")
            }
        }

        DropdownMenu(
            expanded = showExtrasDropdown,
            onDismissRequest = { showExtrasDropdown = false }
        ) {
            dropdownItems(dismissMenu = { showExtrasDropdown = false })
        }
    }
}
