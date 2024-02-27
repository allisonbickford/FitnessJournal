package com.catscoffeeandkitchen.ui.detail.exercise.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeightInput(
    unit: WeightUnit,
    weight: Float,
    onUpdate: (Float) -> Unit,
    onUseKeyboard: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onUseKeyboard) {
            Icon(
                painterResource(id = R.drawable.ic_keyboard),
                "use keyboard"
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (weight >= 10) {
                SuggestionChip(
                    onClick = { onUpdate(weight - 10) },
                    label = { Text((weight - 10).toCleanString() + unit.toAbbreviation()) }
                )
            }

            if (weight >= 5) {
                SuggestionChip(
                    onClick = { onUpdate(weight - 5) },
                    label = { Text((weight - 5).toCleanString() + unit.toAbbreviation()) }
                )
            }

            if (unit == WeightUnit.Kilograms) {
                if (weight >= 2.5f) {
                    SuggestionChip(
                        onClick = { onUpdate(weight - 2.5f) },
                        label = { Text((weight - 2.5f).toCleanString() + unit.toAbbreviation()) }
                    )
                }

                SuggestionChip(
                    onClick = { onUpdate(weight + 2.5f) },
                    label = { Text((weight + 2.5f).toCleanString() + unit.toAbbreviation()) }
                )
            }

            SuggestionChip(
                onClick = { onUpdate(weight + 5) },
                label = { Text((weight + 5).toCleanString() + unit.toAbbreviation()) }
            )

            SuggestionChip(
                onClick = { onUpdate(weight + 10) },
                label = { Text((weight + 10).toCleanString() + unit.toAbbreviation()) }
            )
        }
    }

}

private fun Float.toCleanString(): String {
    return this.toString().replace(".0", "")
}

private fun WeightUnit.toAbbreviation(): String {
    return when (this) {
        WeightUnit.Kilograms -> "kg"
        WeightUnit.Pounds -> "lbs"
    }
}
