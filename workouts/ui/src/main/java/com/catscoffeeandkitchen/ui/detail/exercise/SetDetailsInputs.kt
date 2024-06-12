package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalDragOrCancellation
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.detail.exercise.set.SetActionsRow
import com.catscoffeeandkitchen.ui.detail.exercise.set.SetFieldsSection
import com.catscoffeeandkitchen.ui.detail.plates.BarbellType
import com.catscoffeeandkitchen.ui.detail.plates.PlateCalculator
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme

@Composable
private fun SwipeableSetRow(
    set: ExerciseSet,
    unit: WeightUnit,
    labelColor: Color,
    backgroundColor: Color,
    updateValue: (field: ExerciseSetField) -> Unit,
    removeSet: () -> Unit,
    showPlateCalculator: () -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(-100f) }
    val limit = LocalConfiguration.current.screenWidthDp * .5f

    Box(modifier = Modifier) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SetActionsRow(
                isWarmupSet = set.type == SetType.WarmUp,
                updateValue = updateValue,
                removeSet = removeSet,
                showPlateCalculator = showPlateCalculator,
                onDismiss = { offsetX = -100f }
            )
        }

        SetFieldsSection(
            set,
            unit,
            labelColor = labelColor,
            updateValue = updateValue,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragCancel = { offsetX = -100f },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount < 0 || offsetX < limit) {
                                offsetX += dragAmount
                            }
                        }
                    )
                }
                .offset(x = offsetX.coerceAtLeast(0f).dp, y = 0.dp)
                .background(backgroundColor)
        )
    }
}


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
    var showPlateCalculator by remember { mutableStateOf(false) }

    val labelColor = when {
        set.isComplete -> MaterialTheme.colorScheme.onSurface
        set.type == SetType.WarmUp -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }
    val setColor = when {
        set.isComplete -> MaterialTheme.colorScheme.surfaceContainerHigh
        set.type == SetType.WarmUp -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .background(setColor)
                .fillMaxWidth()
        ) {
            SwipeableSetRow(
                set,
                unit,
                labelColor = labelColor,
                backgroundColor = setColor,
                updateValue = updateValue,
                removeSet = removeSet,
                showPlateCalculator = { showPlateCalculator = true }
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
            if (showPlateCalculator || (!set.isComplete && isBarbellExercise)) {
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


@Preview
@Composable
fun SetItemMultiplePreview() {
    LiftingLogTheme {
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
}
