package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.theme.Spacing
import java.time.OffsetDateTime


@Composable
fun EditableExerciseContent(
    entry: WorkoutEntry,
    goal: Goal?,
    unit: WeightUnit,
    personalBest: WorkoutEntry?,
    onExerciseAction: (ExerciseAction) -> Unit,
    onCompleteExercise: (OffsetDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    val bestSet = personalBest?.sets.orEmpty().maxByOrNull { it.repMax(unit) }

    if (goal != null) {
        Text(
            "${goal.sets}x${goal.minReps} - ${goal.maxReps}reps",
            modifier = Modifier.padding(4.dp)
        )
    }

    if (entry.isSingleSide) {
        Surface(
            modifier = Modifier.padding(vertical = 4.dp),
            shape = SuggestionChipDefaults.shape,
            tonalElevation = 4.dp
        ) {
            Text("Single Side", modifier = Modifier.padding(2.dp))
        }
    }

    Column(
        modifier = modifier
            .animateContentSize()
    ) {
        entry.sets.forEachIndexed { index, set ->
            Box {
                SetDetailsInputs(
                    set,
                    updateValue = { field ->
                        when (field) {
                            is ExerciseSetField.Complete -> {
                                onCompleteExercise(field.value as OffsetDateTime?)

                                onExerciseAction(
                                    ExerciseAction.UpdateSet(entry.id, field.copySetWithNewValue(set))
                                )
                            }
                            is ExerciseSetField.Reps,
                            is ExerciseSetField.WeightInKilograms,
                            is ExerciseSetField.WeightInPounds -> {
                                val setsToPropagateUpdatesTo = entry.sets.filter { item ->
                                    !item.isComplete && item.setNumber >= set.setNumber
                                }

                                onExerciseAction(
                                    ExerciseAction.UpdateSets(entry.id, setsToPropagateUpdatesTo.map { item ->
                                        field.copySetWithNewValue(item)
                                    })
                                )
                            }
                            else -> {
                                onExerciseAction(
                                    ExerciseAction.UpdateSet(entry.id, field.copySetWithNewValue(set))
                                )
                            }
                        }
                    },
                    removeSet = {
                        onExerciseAction(
                            ExerciseAction.RemoveSet(set)
                        )
                    },
                    unit = unit,
                    isNextSet = entry.sets.firstOrNull { !it.isComplete }?.id == set.id,
                    bestSet = bestSet,
                    equipmentType = entry.exercise?.equipment?.firstOrNull()
                )

                if (index != entry.sets.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(.9f)
                            .align(Alignment.BottomCenter),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .5f)
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = {
                onExerciseAction(
                    ExerciseAction.AddSet(entry)
                )
            },
            modifier = Modifier
                .padding(Spacing.Half)
        ) {
            Text(stringResource(R.string.add_set))
        }
    }
}
