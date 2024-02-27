package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.SetModifier
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.theme.Spacing
import java.time.OffsetDateTime

@Composable
fun ExerciseItem(
    entry: WorkoutEntry,
    goal: Goal?,
    unit: WeightUnit,
    isFirstExercise: Boolean,
    isLastExercise: Boolean,
    personalBest: WorkoutEntry?,
    onExerciseAction: (ExerciseAction) -> Unit,
    onNavigationAction: (ExerciseNavigationAction) -> Unit,
    onCompleteSet: (OffsetDateTime?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    var isEditingExercise by remember { mutableStateOf(false) }

    var expandFinishedExercise by remember { mutableStateOf(false) }
    val expandIconRotation by animateFloatAsState(
        targetValue = if (expandFinishedExercise) 0f else 90f
    )

    val exerciseIsFinished = entry.exercise != null
            && entry.sets.isNotEmpty()
            && entry.setsComplete
            && !isEditingExercise

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (exerciseIsFinished) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceContainerHigh
            ),
    ) {
        Column {
            when {
                exerciseIsFinished -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .toggleable(
                                value = expandFinishedExercise,
                                onValueChange = { expandFinishedExercise = it }
                            )
                            .fillMaxWidth()
                            .padding(Spacing.Default)
                    ) {
                        Text(
                            text = entry.exercise?.name.orEmpty(),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            Icons.Default.ArrowDropDown,
                            "Show exercise sets",
                            modifier = Modifier.rotate(expandIconRotation)
                        )
                    }

                    AnimatedVisibility(
                        visible = expandFinishedExercise,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        ReadOnlyExerciseCardContent(
                            entry = entry,
                            unit = unit,
                            modifier = Modifier
                                .clickable {
                                    isEditingExercise = true
                                }
                                .padding(top = Spacing.Half)
                        )
                    }
                }
                entry.exercise != null -> {
                    ExerciseHeader(
                        title = entry.exercise?.name.orEmpty(),
                        showDropdownMenu = showDropdownMenu,
                        onShowDropdownMenu = { showDropdownMenu = it },
                        onClickHeader = if (isEditingExercise) {
                            {
                                expandFinishedExercise = false
                                isEditingExercise = false
                            }
                        } else {
                            null
                        }
                    ) {
                        ExerciseMenuItems(
                            entry,
                            unit,
                            isFirstExercise,
                            isLastExercise,
                            onExerciseAction,
                            onNavigationAction,
                            onDismiss = { showDropdownMenu = false }
                        )
                    }

                    EditableExerciseContent(
                        entry,
                        goal,
                        unit,
                        personalBest = personalBest,
                        onExerciseAction = onExerciseAction,
                        onCompleteExercise = { time ->
                            isEditingExercise = false
                            onCompleteSet(time)
                        }
                    )
                }
                entry.group != null -> {
                    ExerciseHeader(
                        title = (goal?.group?.name ?: "")
                            .ifEmpty { "Select from group" },
                        showDropdownMenu = showDropdownMenu,
                        onShowDropdownMenu = { showDropdownMenu = it },
                        onClickHeader = if (isEditingExercise) {
                            {
                                expandFinishedExercise = false
                                isEditingExercise = false
                            }
                        } else {
                            null
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("edit group") },
                            onClick = {
                                goal?.group?.let { group ->
                                    onNavigationAction(ExerciseNavigationAction.EditGroup(group))
                                }
                            })
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        entry.group?.exercises?.forEach { exercise ->
                            GroupExerciseItem(exercise, onSelect = { selected ->
                                onExerciseAction(ExerciseAction.SelectExerciseFromGroup(
                                    selected,
                                    entry
                                ))
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseMenuItems(
    entry: WorkoutEntry,
    unit: WeightUnit,
    isFirstExercise: Boolean,
    isLastExercise: Boolean,
    onExerciseAction: (ExerciseAction) -> Unit,
    onNavigationAction: (ExerciseNavigationAction) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenuItem(
        leadingIcon = { Icon(painterResource(R.drawable.fireplace), "create warm up sets") },
        text = { Text("add pyramid warmup") },
        onClick = {
            onExerciseAction(ExerciseAction.AddWarmupSets(
                entry,
                unit
            ))
            onDismiss()
        })
    DropdownMenuItem(
        leadingIcon = { Icon(Icons.Default.Delete, "remove exercise") },
        text = { Text("remove") },
        onClick = {
            onExerciseAction(ExerciseAction.RemoveEntry(entry))
            onDismiss()
        })

    DropdownMenuItem(
        leadingIcon = { Icon(Icons.Default.Refresh, "swap exercise") },
        text = { Text("swap") },
        onClick = {
            onNavigationAction(
                ExerciseNavigationAction.SwapExerciseAt(entry.position)
            )
            onDismiss()
        })

    if (entry.group != null) {
        DropdownMenuItem(
            leadingIcon = { Icon(painterResource(R.drawable.checklist), "show group") },
            text = { Text("choose from group") },
            onClick = {
                onExerciseAction(ExerciseAction.ReplaceWithGroup(entry))
                onDismiss()
            })
    }

    DropdownMenuItem(
        leadingIcon = { Icon(Icons.Default.Check, "mark as single arm/leg") },
        text = { Text("${if (entry.isSingleSide) "un" else ""}mark as single arm/leg") },
        onClick = {
            if (entry.isSingleSide) {
//                setActions?.updateSets(uiData.entry.sets.map { it.copy(modifier = null) })
            } else {
//                setActions?.updateSets(uiData.entry.sets.map { it.copy(modifier = ExerciseSetModifier.SingleSide) })
            }
            onDismiss()
        })

    if (!isFirstExercise) {
        DropdownMenuItem(
            leadingIcon = { Icon(Icons.Default.KeyboardArrowUp, "move up") },
            text = { Text("move up") },
            onClick = {
                onExerciseAction(ExerciseAction.MoveEntryTo(entry, entry.position - 1))
                onDismiss()
            })
    }

    if (!isLastExercise) {
        DropdownMenuItem(
            leadingIcon = { Icon(Icons.Default.KeyboardArrowDown, "move down") },
            text = { Text("move down") },
            onClick = {
                onExerciseAction(ExerciseAction.MoveEntryTo(entry, entry.position + 1))
                onDismiss()
            })
    }
}

@Composable
fun ExerciseHeader(
    title: String,
    showDropdownMenu: Boolean,
    onShowDropdownMenu: (Boolean) -> Unit,
    onClickHeader: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Spacing.Default
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .then(if (onClickHeader != null) Modifier.clickable { onClickHeader() } else Modifier)
                .padding(vertical = Spacing.Half)
                .weight(1f)
        )

        Box {
            IconButton(
                onClick = { onShowDropdownMenu(!showDropdownMenu) },
            ) {
                Icon(Icons.Default.MoreVert, "more exercise options")
            }

            DropdownMenu(
                expanded = showDropdownMenu,
                onDismissRequest = { onShowDropdownMenu(false) }
            ) {
                content()
            }
        }
    }
}
