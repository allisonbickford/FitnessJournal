package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.detail.exercise.set.SetFieldsSection
import com.catscoffeeandkitchen.ui.detail.plates.BarbellType
import com.catscoffeeandkitchen.ui.detail.plates.PlateCalculator
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun SetDetailsInputs(
    set: ExerciseSet,
    unit: WeightUnit,
    equipmentType: EquipmentType?,
    isNextSet: Boolean,
    bestSet: ExerciseSet?,
    modifier: Modifier = Modifier,
    updateValue: (field: ExerciseSetField) -> Unit = { },
    removeSet: () -> Unit = {},
) {
    var shouldShowPlateCalculator by remember { mutableStateOf(false) }

    val labelColor = when {
        set.isComplete -> MaterialTheme.colorScheme.onBackground
        set.type == SetType.WarmUp -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onPrimary
    }

    var showOptionsMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ExerciseSetDropdownMenu(
            set = set,
            isVisible = showOptionsMenu,
            onDismiss = { showOptionsMenu = false },
            removeSet = {
                showOptionsMenu = false
                removeSet()
            },
            updateValue = { field ->
                updateValue(field)
                showOptionsMenu = false
            },
            showPlateCalculator = {
                shouldShowPlateCalculator = true
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.Half)
        ) {
            if (set.type == SetType.WarmUp) {
                Text(
                    stringResource(id = R.string.warm_up),
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    modifier = Modifier
                        .padding(start = Spacing.Default)
                )
            }

            SetFieldsSection(
                set,
                unit,
                labelColor = labelColor,
                updateValue = updateValue,
                onOpenOptions = {
                    showOptionsMenu = true
                }
            )

            if (isNextSet) {
                NextPRText(
                    nextSet = set,
                    bestSet = bestSet,
                    unit = unit,
                    color = labelColor
                )
            }

            val isBarbellExercise = isNextSet && equipmentType == EquipmentType.Barbell
            if (shouldShowPlateCalculator || (!set.isComplete && isBarbellExercise)) {
                PlateCalculator(
                    barbell = if (isBarbellExercise) BarbellType.Standard else BarbellType.None,
                    weight = if (unit == WeightUnit.Pounds) set.weightInPounds.toDouble()
                        else set.weightInKilograms.toDouble(),
                    unit = unit,
                )
            }
        }
    }
}


@Preview(
    name = "Grid"
)
@Composable
fun SetItemGridPreview() {
    Card {
        SetDetailsInputs(
            set = ExerciseSet(
                id = 0L,
                reps = 4,
                setNumber = 1,
                weightInPounds = 140f,
                repsInReserve = 3,
                perceivedExertion = 7,
                type = SetType.WarmUp
            ),
            unit = WeightUnit.Pounds,
            isNextSet = false,
            bestSet = null,
            equipmentType = EquipmentType.Barbell,
        )

        SetDetailsInputs(
            set = ExerciseSet(
                id = 0L,
                reps = 4,
                setNumber = 1,
                weightInPounds = 140f,
                repsInReserve = 3,
                perceivedExertion = 7
            ),
            unit = WeightUnit.Pounds,
            isNextSet = false,
            bestSet = null,
            equipmentType = EquipmentType.Barbell
        )
    }
}
