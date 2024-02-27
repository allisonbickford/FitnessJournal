package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.settings.ui.R

@Composable
fun UnitsSection(
    unit: WeightUnit,
    onUpdate: (WeightUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(R.string.weight_unit),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            stringResource(R.string.weight_unit_settings_description)
        )

        TextSwitch(
            selectedIndex = unit.ordinal,
            onSelectIndex = { onUpdate(WeightUnit.entries[it]) },
            options = WeightUnit.entries.map { it.name.lowercase() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
