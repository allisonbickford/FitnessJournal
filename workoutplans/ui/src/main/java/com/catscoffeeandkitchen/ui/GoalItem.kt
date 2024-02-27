package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.models.SetAmount
import com.catscoffeeandkitchen.models.SetModifier
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun GoalItem(
    goal: Goal,
    onGoalAction: (PlanAction) -> Unit,
    isFirstSet: Boolean = false,
    isLastSet: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(Spacing.Half)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.Half),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Spacing.Half)
            ) {
                val name: String = when {
                    goal.exercise != null -> goal.exercise!!.name
                    goal.group?.name != null -> goal.group?.name.orEmpty()
                    goal.group != null -> goal.group!!.exercises.joinToString { it.name }
                    else -> "Exercise"
                }
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        onGoalAction(
                            PlanAction.RepositionGoal(goal, goal.position - 1)
                        )
                    },
                    enabled = !isFirstSet,
                    modifier = Modifier.weight(.33f)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, "move exercise up")
                }

                IconButton(
                    onClick = {
                        onGoalAction(
                            PlanAction.RepositionGoal(goal, goal.position + 1)
                        )
                    },
                    enabled = !isLastSet,
                    modifier = Modifier.weight(.33f),
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, "move exercise down")
                }

                IconButton(
                    onClick = {
                        onGoalAction(PlanAction.RemoveGoal(goal.id))
                    },
                    modifier = Modifier.weight(.33f),
                ) {
                    Icon(Icons.Default.Delete, "delete exercise")
                }
            }

            if (!goal.group?.exercises.isNullOrEmpty()) {
                Text(
                    goal.group?.exercises?.joinToString { it.name } ?: "Group",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = Spacing.Half)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.Half),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Half)
            ) {
                FilterChip(
                    selected = goal.modifiers.contains(SetModifier.SingleSide),
                    onClick = {
                        onGoalAction(PlanAction.UpdateGoal(goal.copy(modifiers =
                            if (goal.modifiers.contains(SetModifier.SingleSide)) {
                                goal.modifiers.minus(SetModifier.SingleSide)
                            } else {
                                goal.modifiers.plus(SetModifier.SingleSide)
                            }
                        )))
                    },
                    label = { Text("Single Side") },
                    leadingIcon = {
                        if (goal.modifiers.contains(SetModifier.SingleSide)) {
                            Icon(Icons.Default.Check, "selected")
                        }
                    }
                )

                FilterChip(
                    selected = goal.modifiers.contains(SetModifier.Timed),
                    onClick = {
                        val updatedGoal = goal.copy(modifiers =
                            if (goal.modifiers.contains(SetModifier.Timed)) {
                                goal.modifiers.minus(SetModifier.Timed)
                            } else {
                                goal.modifiers.plus(SetModifier.Timed)
                            }
                        )
                        onGoalAction(PlanAction.UpdateGoal(updatedGoal))
                    },
                    label = { Text("Timed") },
                    leadingIcon = {
                        if (goal.modifiers.contains(SetModifier.Timed)) {
                            Icon(Icons.Default.Check, "selected")
                        }
                    }
                )

                FilterChip(
                    selected = goal.setAmount == SetAmount.Drop,
                    onClick = {
                        val updatedGoal = if (goal.setAmount == SetAmount.Drop) {
                            goal.copy(
                                setAmount = SetAmount.Drop,
                                sets = 3,
                                reps = 7,
                                minReps = 6,
                                maxReps = 8
                            )
                        } else {
                            goal.copy(
                                setAmount = SetAmount.Fixed,
                            )
                        }
                        onGoalAction(PlanAction.UpdateGoal(updatedGoal))
                    },
                    label = { Text("Drop Set") },
                    leadingIcon = {
                        if (goal.setAmount == SetAmount.Drop) {
                            Icon(Icons.Default.Check, "selected")
                        }
                    }
                )
            }

            EditExercisePlanGrid(
                goal,
                onGoalAction = onGoalAction,
                modifier = Modifier.padding(
                    horizontal = Spacing.Half
                )
            )
        }
    }
}

@Preview
@Composable
private fun ExercisePlanItemPreview() {
    LiftingLogTheme {
        var goal by remember { mutableStateOf(
            Goal(
                id = 1,
                exercise = Exercise(
                    id = 1,
                    name = "Bicep Curl",
                    category = MuscleCategory.Arms,
                    musclesWorked = listOf("Bicep")
                ),
                group = null,
                position = 1,
                note = "go until fatigue",
                reps = 10,
                minReps = 6,
                maxReps = 10,
                sets = 3,
                setAmount = SetAmount.Fixed,
                weightInPounds = 30.0f,
                weightInKilograms = 10.0f,
                modifiers = listOf(SetModifier.SingleSide),
                equipment = listOf(EquipmentType.Dumbell)
            )
        ) }

        var groupedGoal by remember { mutableStateOf(
            Goal(
                id = 1,
                exercise = null,
                group = ExerciseGroup(
                    id = 1,
                    name = "Presses",
                    exercises = listOf(
                        Exercise(id = 2, name = "Bench Press"),
                        Exercise(id = 3, name = "Incline Bench Press"),
                        Exercise(id = 4, name = "Arnold Press"),
                        Exercise(id = 4, name = "Military Press"),
                    )
                ),
                position = 1,
                note = null,
                reps = 10,
                minReps = 6,
                maxReps = 10,
                sets = 3,
                setAmount = SetAmount.Fixed,
                weightInPounds = 30.0f,
                weightInKilograms = 10.0f,
                modifiers = emptyList(),
                equipment = listOf(EquipmentType.Dumbell)
            )
        ) }

        Column {
            GoalItem(
                goal = goal.copy(modifiers = listOf(SetModifier.SingleSide)),
                onGoalAction = { action ->

                },
                isFirstSet = true,
                isLastSet = false,
                onClick = { }
            )

            GoalItem(
                goal = goal.copy(modifiers = listOf(SetModifier.Timed)),
                onGoalAction = { action ->

                },
                isFirstSet = true,
                isLastSet = false,
                onClick = { }
            )

            GoalItem(
                goal = groupedGoal,
                onGoalAction = { action ->

                },
                isFirstSet = true,
                isLastSet = false,
                onClick = { }
            )
        }
    }
}
