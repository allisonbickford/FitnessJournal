package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.SetModifier
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import com.catscoffeeandkitchen.workoutplans.ui.R

@Composable
fun EditExercisePlanGrid(
    goal: Goal,
    onGoalAction: (PlanAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Default)
    ) {
        InputWithLabel(
            value = goal.sets,
            onValueChanged = { onGoalAction(PlanAction.UpdateGoal(goal.copy(sets = it))) },
            label = stringResource(R.string.sets)
        )

        if (goal.modifiers.contains(SetModifier.Timed)) {
            InputWithLabel(
                value = goal.seconds,
                onValueChanged = {
                    onGoalAction(PlanAction.UpdateGoal(goal.copy(seconds = it)))
                },
                label = stringResource(R.string.seconds)
            )

            InputWithLabel(
                value = goal.minSeconds,
                onValueChanged = {
                    onGoalAction(PlanAction.UpdateGoal(goal.copy(minSeconds = it)))
                },
                label = stringResource(R.string.min_seconds)
            )

            InputWithLabel(
                value = goal.maxSeconds,
                onValueChanged = {
                    onGoalAction(PlanAction.UpdateGoal(goal.copy(maxSeconds = it)))
                },
                label = stringResource(R.string.max_seconds)
            )
        } else {
            InputWithLabel(
                value = goal.reps,
                onValueChanged = { onGoalAction(PlanAction.UpdateGoal(goal.copy(reps = it))) },
                label = stringResource(R.string.reps)
            )

            InputWithLabel(
                value = goal.minReps,
                onValueChanged = { onGoalAction(PlanAction.UpdateGoal(goal.copy(minReps = it))) },
                label = stringResource(R.string.min_reps)
            )

            InputWithLabel(
                value = goal.maxReps,
                onValueChanged = { onGoalAction(PlanAction.UpdateGoal(goal.copy(maxReps = it))) },
                label = stringResource(R.string.max_reps)
            )
        }

    }
}

@Composable
private fun InputWithLabel(
    value: Int?,
    onValueChanged: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    val focusManager = LocalFocusManager.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value?.toString().orEmpty())) }

    Column {
        BasicTextField(
            value = textFieldValue,
            singleLine = true,
            onValueChange = { textFieldValue = it  },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            modifier = modifier
                .requiredWidth(60.dp)
                .onFocusChanged { state ->
                    if (state.hasFocus) {
                        textFieldValue = textFieldValue.copy(
                            selection = TextRange(0, textFieldValue.text.length)
                        )
                    } else {
                        textFieldValue.text.takeIf { it.isNotBlank() }?.toIntOrNull()?.let {
                            onValueChanged(it)
                        }
                    }
                },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = if (imeAction != ImeAction.Done) null else {
                    {
                        textFieldValue.text.takeIf { it.isNotBlank() }?.toIntOrNull()?.let {
                            onValueChanged(it)
                        }
                    }
                },
                onNext = if (imeAction != ImeAction.Next) null else {
                    {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                },
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        )

        Text(
            label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview
@Composable
fun SetItemGridPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            EditExercisePlanGrid(
                goal = Goal(
                    exercise = Exercise(
                        "Bicep Curls",
                        musclesWorked = listOf("Biceps"),
                    ),
                    position = 1
                ),
                onGoalAction = { _ -> }
            )
        }
    }
}
