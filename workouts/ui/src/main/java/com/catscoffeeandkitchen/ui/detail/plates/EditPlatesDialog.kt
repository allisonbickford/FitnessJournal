package com.catscoffeeandkitchen.ui.detail.plates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.R


@Composable
fun PlateDialog(
    plateSettings: PlateCalculatorHelper.PlateSettings,
    plateResults: PlateCalculatorHelper.PlateResults,
    onDismissRequest: () -> Unit = {},
    updatePlateSettings: (PlateCalculatorHelper.PlateSettings) -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        PlateDialogContent(
            plateSettings = plateSettings,
            plateResults = plateResults
        ) { amount, plateWeight ->
            val updatedAmounts = plateSettings.amounts.toMutableMap()
            updatedAmounts[plateWeight] = amount

            updatePlateSettings(plateSettings.copy(amounts = updatedAmounts))
        }
    }
}

@Composable
fun PlateEditColumn(
    plateAmount: Int = 100,
    plateWeight: Double,
    unit: WeightUnit,
    onUpdate: (Int) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            if (unit == WeightUnit.Kilograms)
                stringResource(id = R.string.plate_kg, plateWeight.toString())
            else
                stringResource(id = R.string.plate_lbs, plateWeight.toString()),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                enabled = plateAmount > 0,
                onClick = { onUpdate(plateAmount - 1) }
            ) {
                Icon(painterResource(R.drawable.remove), "remove plate")
            }

            Text(
                plateAmount.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(6.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
            )

            IconButton(onClick = { onUpdate(plateAmount + 1) }) {
                Icon(Icons.Default.Add, "add plate")
            }
        }
    }
}


@Composable
fun PlateDialogContent(
    plateSettings: PlateCalculatorHelper.PlateSettings,
    plateResults: PlateCalculatorHelper.PlateResults,
    updatePlateAmount: (amount: Int, weight: Double) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        Text(
            stringResource(R.string.plates_to_use),
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        plateSettings.amounts.entries.forEach { entry ->
            PlateEditColumn(
                plateAmount = entry.value,
                plateWeight = entry.key,
                unit = plateSettings.unit
            ) { updatePlateAmount(it, entry.key) }
        }

        if (plateResults.leftoverWeight != 0.0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp)
                    .align(Alignment.End),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (plateSettings.unit == WeightUnit.Kilograms)
                        stringResource(id = R.string.kgs_needed, plateResults.leftoverWeight.toString())
                    else
                        stringResource(id = R.string.lbs_needed, plateResults.leftoverWeight.toString()),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
