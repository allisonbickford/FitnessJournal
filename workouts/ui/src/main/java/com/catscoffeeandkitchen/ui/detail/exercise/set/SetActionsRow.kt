package com.catscoffeeandkitchen.ui.detail.exercise.set

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.components.LabeledIconButton
import com.catscoffeeandkitchen.ui.detail.exercise.ExerciseSetField
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing


@Composable
fun SetActionsRow(
    isWarmupSet: Boolean,
    updateValue: (field: ExerciseSetField) -> Unit,
    removeSet: () -> Unit,
    showPlateCalculator: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Spacing.Default,
                end = Spacing.Default,
                bottom = Spacing.Default
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Half)
    ) {
        if (isWarmupSet) {
            LabeledIconButton(
                label = "Working",
                icon = {
                    Icon(painterResource(R.drawable.whatshot), contentDescription = "mark as working")
                },
                onClick = {
                    updateValue(ExerciseSetField.Type(SetType.Working))
                    onDismiss()
                }
            )
        } else {
            LabeledIconButton(
                label = "Warmup",
                icon = {
                    Icon(painterResource(R.drawable.fireplace), contentDescription = "mark as warmup")
                },
                onClick = {
                    updateValue(ExerciseSetField.Type(SetType.WarmUp))
                    onDismiss()
                }
            )
        }

        LabeledIconButton(
            label = "Delete",
            icon = {
                Icon(Icons.Default.Delete, contentDescription = "delete set")
            },
            onClick = removeSet
        )

        LabeledIconButton(
            label = "Plates",
            icon = {
                Icon(
                    painterResource(com.catscoffeeandkitchen.common.ui.R.drawable.fitness_center),
                    "Show plate calculator"
                )
            },
            onClick = {
                showPlateCalculator()
                onDismiss()
            }
        )
    }
}

@Preview
@Composable
private fun SetActionModalPreview() {
    LiftingLogTheme {
        Scaffold { paddingValues ->
            Column(modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                SetActionsRow(
                    isWarmupSet = true,
                    updateValue = { },
                    removeSet = { },
                    showPlateCalculator = { },
                    onDismiss = { },
                )
            }
        }
    }
}
