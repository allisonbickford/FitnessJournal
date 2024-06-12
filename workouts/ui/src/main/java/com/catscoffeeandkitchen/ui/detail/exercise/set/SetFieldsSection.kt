package com.catscoffeeandkitchen.ui.detail.exercise.set

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.abbreviation
import com.catscoffeeandkitchen.ui.detail.exercise.ExerciseSetField
import com.catscoffeeandkitchen.ui.detail.exercise.InputToDisplay
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import com.catscoffeeandkitchen.ui.toCleanString
import java.time.OffsetDateTime

@OptIn(ExperimentalFoundationApi::class)
@Suppress("CyclomaticComplexMethod")
@Composable
fun SetFieldsSection(
    set: ExerciseSet,
    unit: WeightUnit,
    labelColor: Color,
    updateValue: (ExerciseSetField) -> Unit,
    modifier: Modifier = Modifier,
) {
    var openInput by remember { mutableStateOf(null as InputToDisplay?) }
    var useKeyboard by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = Spacing.Quarter)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Checkbox(
                checked = set.isComplete,
                onCheckedChange = { checked ->
                    useKeyboard = false
                    updateValue(
                        ExerciseSetField.Complete(if (checked) OffsetDateTime.now() else null),
                    )
                },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = labelColor
                )
            )

            SetInput(
                value = set.reps.toString(),
                label = "reps",
                labelColor = labelColor,
                isSetComplete = set.isComplete,
                useKeyboard = useKeyboard && openInput == InputToDisplay.Reps,
                onFocus = {
                    openInput = if (openInput != InputToDisplay.Reps) {
                        InputToDisplay.Reps
                    } else {
                        null
                    }
                },
                updateValue = { value ->
                    updateValue(ExerciseSetField.Reps(value.toIntOrNull() ?: 0))
                    useKeyboard = false
                    openInput = null
                }
            )

            SetInput(
                value = set.weight(unit).toCleanString(),
                label = unit.abbreviation(),
                labelColor = labelColor,
                isSetComplete = set.isComplete,
                updateValue = { value ->
                    if (unit == WeightUnit.Pounds) {
                        updateValue(ExerciseSetField.WeightInPounds(value.toFloatOrNull() ?: 0f))
                    } else {
                        updateValue(ExerciseSetField.WeightInKilograms(value.toFloatOrNull() ?: 0f))
                    }
                    useKeyboard = false
                    openInput = null
                },
                useKeyboard = useKeyboard && openInput == InputToDisplay.Weight,
                onFocus = {
                    openInput = if (openInput != InputToDisplay.Weight) {
                        InputToDisplay.Weight
                    } else {
                        null
                    }
                }
            )

            SetInput(
                value = set.repsInReserve.toString(),
                label = "RIR",
                useKeyboard = useKeyboard && openInput == InputToDisplay.RIR,
                updateValue = { value ->
                    updateValue(ExerciseSetField.RepsInReserve(value.toIntOrNull() ?: 0))
                    useKeyboard = false
                    openInput = null
                },
                labelColor = labelColor,
                isSetComplete = set.isComplete,
                onFocus = {
                    openInput = if (openInput != InputToDisplay.RIR) {
                        InputToDisplay.RIR
                    } else {
                        null
                    }
                }
            )

            SetInput(
                value = set.perceivedExertion.toString(),
                label = "PE",
                updateValue = { value ->
                    updateValue(ExerciseSetField.PerceivedExertion(value.toIntOrNull() ?: 0))
                    useKeyboard = false
                    openInput = null
                },
                labelColor = labelColor,
                isSetComplete = set.isComplete,
                useKeyboard = useKeyboard && openInput == InputToDisplay.PE,
                onFocus = {
                    openInput = if (openInput != InputToDisplay.PE) {
                        InputToDisplay.PE
                    } else {
                        null
                    }
                }
            )
        }

        if (set.type == SetType.WarmUp) {
            Text(
                stringResource(id = R.string.warm_up),
                style = MaterialTheme.typography.labelSmall,
                color = labelColor,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        top = Spacing.Quarter,
                        start = Spacing.Half
                    )
            )
        }
    }

    SetInputButtonsSection(
        inputOpen = openInput != null && !useKeyboard,
        input = openInput,
        unit = unit,
        set = set,
        updateValue = updateValue,
        closeInput = {
            openInput = null
        },
        onUseKeyboard = {
            useKeyboard = true
        }
    )
}

@Preview
@Composable
fun SetFieldsSectionPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            SetFieldsSection(
                set = ExerciseSet(
                    id = 1L,
                    reps = 10
                ),
                unit = WeightUnit.Pounds,
                labelColor = MaterialTheme.colorScheme.onBackground,
                updateValue = { }
            )
        }
    }
}

@Preview
@Composable
fun SetFieldsSectionPreviewDark() {
    LiftingLogTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            SetFieldsSection(
                set = ExerciseSet(
                    id = 1L,
                    reps = 10,
                    type = SetType.WarmUp
                ),
                unit = WeightUnit.Pounds,
                labelColor = MaterialTheme.colorScheme.onBackground,
                updateValue = { }
            )
        }
    }
}

