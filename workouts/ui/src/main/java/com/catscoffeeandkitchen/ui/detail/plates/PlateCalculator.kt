package com.catscoffeeandkitchen.ui.detail.plates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.components.LLButton
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun PlateCalculator(
    barbell: BarbellType,
    weight: Double,
    unit: WeightUnit,
) {
    var showPlateDialog by remember { mutableStateOf(false) }
    var settings by remember {
        mutableStateOf(PlateCalculatorHelper.PlateSettings(amounts = mapOf(), unit = unit))
    }

    val plateHelper = PlateCalculatorHelper()
    var plates = plateHelper.calculatePlates(barbell, weight, settings)

    if (showPlateDialog) {
        PlateDialog(
            plateSettings = if (settings.amounts.isEmpty())
                settings.copy(amounts = plates.amounts.mapValues { entry ->
                    entry.value.takeIf { value -> value >= 0 } ?: 0 }
                )
            else settings,
            plateResults = plates,
            onDismissRequest = { showPlateDialog = false },
            updatePlateSettings = { updatedSettings ->
                settings = updatedSettings
                plates = plateHelper.calculatePlates(barbell, weight, updatedSettings)
            }
        )
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = Spacing.Half)
        ) {
            PlateStackVertical(
                plates = plates.amounts
            )

            Spacer(modifier = Modifier.padding(start = Spacing.Default))

            LLButton(onClick = { showPlateDialog = true }) {
                Text(stringResource(R.string.change_plates))
            }
        }

        if (plates.leftoverWeight > 0) {
            Card(
                colors = CardDefaults.cardColors().copy(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text("${plates.leftoverWeight} more " +
                        "${unit.name.lowercase()} on each side needed to reach this weight.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(Spacing.Half)
                )
            }
        }
    }
}
