package com.catscoffeeandkitchen.ui.detail.exercise.set

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.catscoffeeandkitchen.ui.detail.exercise.inputs.ExerciseSetButtonInput
import com.catscoffeeandkitchen.ui.detail.exercise.inputs.ExerciseSetInputWithLabel


@Composable
fun SetInput(
    value: String,
    isSetComplete: Boolean,
    useKeyboard: Boolean,
    label: String,
    labelColor: Color,
    updateValue: (value: String) -> Unit,
    onFocus: () -> Unit
) {
    if (useKeyboard) {
        ExerciseSetInputWithLabel(
            value = value,
            label = label,
            labelColor = labelColor,
            enabled = !isSetComplete,
            updateValue = updateValue
        )
    } else {
        ExerciseSetButtonInput(
            value = value,
            label = label,
            labelColor = labelColor,
            backgroundColor = if (isSetComplete) Color.Transparent
                                else MaterialTheme.colorScheme.surface,
            enabled = !isSetComplete,
            horizontalBias = if (isSetComplete) 0f else -1f,
            onClick = onFocus
        )
    }
}
