package com.catscoffeeandkitchen.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.WorkoutPlan
import com.catscoffeeandkitchen.ui.AddExerciseOrGroupButtons
import com.catscoffeeandkitchen.ui.GoalItem
import com.catscoffeeandkitchen.ui.PlanAction
import com.catscoffeeandkitchen.ui.WeekRow
import com.catscoffeeandkitchen.ui.components.LLButton
import com.catscoffeeandkitchen.ui.theme.Spacing
import com.catscoffeeandkitchen.workoutplans.ui.R

fun LazyListScope.planItems(
    plan: WorkoutPlan,
    onPlanAction: (PlanAction) -> Unit,
    onOpenGroup: (Long) -> Unit,
    onOpenExercise: (Long) -> Unit
) {
    item {
        var editedPlanName by remember { mutableStateOf(plan.name) }
        var isEditingName by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        OutlinedTextField(
            value = editedPlanName,
            onValueChange = { editedPlanName = it },
            label = { Text(stringResource(R.string.plan_name)) },
            trailingIcon = {
                AnimatedVisibility(visible = isEditingName) {
                    IconButton(
                        onClick = {
                            if (editedPlanName != plan.name) {
                                onPlanAction(PlanAction.UpdateWorkoutName(editedPlanName))
                            }
                            focusManager.clearFocus()
                        }
                    ) {
                        Icon(Icons.Default.Check, "change plan name")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Default)
                .onFocusChanged { state ->
                    isEditingName = state.isFocused
                }
        )

        Spacer(Modifier.padding(top = Spacing.Half))
    }

    item {
        var editedNote by remember { mutableStateOf(plan.note.orEmpty()) }
        var isEditingNote by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        OutlinedTextField(
            value = editedNote,
            onValueChange = { editedNote = it },
            singleLine = false,
            label = { Text(stringResource(R.string.note)) },
            trailingIcon = {
                AnimatedVisibility(visible = isEditingNote) {
                    IconButton(
                        onClick = {
                            if (editedNote != plan.note) {
                                onPlanAction(PlanAction.UpdateWorkoutNotes(editedNote))
                            }
                            focusManager.clearFocus()
                        }
                    ) {
                        Icon(Icons.Default.Check, "Update workout note")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Default)
                .onFocusChanged { state ->
                    isEditingNote = state.isFocused
                }
        )

        Spacer(Modifier.padding(top = Spacing.Half))
    }

    item {
        WeekRow(
            plan.daysOfWeek,
            onWeekdaySelected = { day ->
                onPlanAction(
                    PlanAction.UpdateWeekdays(
                        if (plan.daysOfWeek.contains(day)) {
                            plan.daysOfWeek.filter { it != day }
                        } else {
                            plan.daysOfWeek + listOf(day)
                        }
                    )
                )
            },
            modifier = Modifier.padding(horizontal = Spacing.Default)
        )

        Spacer(Modifier.padding(top = Spacing.Half))
    }

    goalItems(
        plan.goals, onOpenGroup, onOpenExercise, onPlanAction
    )
}

private fun LazyListScope.goalItems(
    goals: List<Goal>,
    onOpenGroup: (Long) -> Unit,
    onOpenExercise: (Long) -> Unit,
    onPlanAction: (PlanAction) -> Unit
) {
    items(goals) { goal ->
        if (goal.id == goals.firstOrNull()?.id) {
            HorizontalDivider()
        }

        GoalItem(
            goal,
            onClick = {
                if (goal.group != null) {
                    goal.group?.id?.let { onOpenGroup(it) }
                } else if (goal.exercise != null) {
                    goal.exercise?.id?.let { onOpenExercise(it) }
                }
            },
            onGoalAction = onPlanAction,
            isFirstSet = goals.minOfOrNull { it.position } == goal.position,
            isLastSet = goals.maxOfOrNull { it.position } == goal.position
        )

        HorizontalDivider()
    }

    item {
        Spacer(Modifier.padding(top = Spacing.Half))

        AddExerciseOrGroupButtons(
            addExercise = { onPlanAction(PlanAction.AddExercise) },
            addGroup = { onPlanAction(PlanAction.AddExerciseGroup) },
            modifier = Modifier.padding(horizontal = Spacing.Default)
        )
    }

    item {
        LLButton(
            onClick = {
                onPlanAction(PlanAction.StartWorkout)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Default)
        ) {
            Text(stringResource(R.string.start_workout))
        }
    }
}
