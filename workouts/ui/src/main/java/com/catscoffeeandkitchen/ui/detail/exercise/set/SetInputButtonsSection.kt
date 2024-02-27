package com.catscoffeeandkitchen.ui.detail.exercise.set

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.detail.exercise.ExerciseSetField
import com.catscoffeeandkitchen.ui.detail.exercise.InputToDisplay
import com.catscoffeeandkitchen.ui.detail.exercise.inputs.PerceivedExertionInput
import com.catscoffeeandkitchen.ui.detail.exercise.inputs.RepsInReserveInput
import com.catscoffeeandkitchen.ui.detail.exercise.inputs.RepsInput
import com.catscoffeeandkitchen.ui.detail.exercise.inputs.WeightInput

@Composable
fun SetInputButtonsSection(
    inputOpen: Boolean,
    input: InputToDisplay?,
    unit: WeightUnit,
    set: ExerciseSet,
    updateValue: (ExerciseSetField) -> Unit,
    closeInput: () -> Unit,
    onUseKeyboard: () -> Unit,
) {
    AnimatedVisibility(
        visible = inputOpen,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        when (input) {
            InputToDisplay.Reps -> {
                RepsInput(
                    set.reps,
                    onUpdate = { reps ->
                        updateValue(ExerciseSetField.Reps(reps))
                        closeInput()
                    },
                    onUseKeyboard = onUseKeyboard
                )
            }
            InputToDisplay.RIR -> {
                RepsInReserveInput(
                    rir = set.repsInReserve,
                    onUpdate = { rir ->
                        updateValue(ExerciseSetField.RepsInReserve(rir))
                        closeInput()
                    },
                    onUseKeyboard = onUseKeyboard
                )
            }
            InputToDisplay.Weight -> {
                WeightInput(
                    unit,
                    if (unit == WeightUnit.Kilograms)
                        set.weightInKilograms else set.weightInPounds,
                    onUpdate = { weight ->
                        if (unit == WeightUnit.Kilograms) {
                            updateValue(ExerciseSetField.WeightInKilograms(weight))
                        } else {
                            updateValue(ExerciseSetField.WeightInPounds(weight))
                        }
                        closeInput()
                    },
                    onUseKeyboard = onUseKeyboard
                )
            }
            InputToDisplay.PE -> {
                PerceivedExertionInput(
                    pe = set.perceivedExertion,
                    onUpdate = { pe ->
                        updateValue(ExerciseSetField.PerceivedExertion(pe))
                        closeInput()
                    },
                    onUseKeyboard = onUseKeyboard
                )
            }
            else -> {
                // show nothing
            }
        }
    }
}
