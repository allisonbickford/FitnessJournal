package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.theme.Spacing
import java.time.OffsetDateTime

@Composable
fun EntrySets(
    entry: WorkoutEntry,
    goal: Goal?,
    unit: WeightUnit,
    personalBest: WorkoutEntry?,
    onExerciseAction: (ExerciseAction) -> Unit,
    onCompleteSet: (OffsetDateTime?) -> Unit,
    onOpenEntryModal: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            )
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
                    Text(
                        text = entry.exercise?.name.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .clickable {
                                onOpenEntryModal()
                            }
                            .padding(Spacing.Half)
                            .fillMaxWidth()
                    )

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
                    Text(
                        text = (goal?.group?.name ?: "")
                            .ifEmpty { "Select from group" },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(Spacing.Half)
                            .fillMaxWidth()
                    )

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
