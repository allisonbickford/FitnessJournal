package com.catscoffeeandkitchen.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.catscoffeeandkitchen.common.ui.R
import com.catscoffeeandkitchen.models.WeightUnit

@Composable
fun WeightUnit.abbreviation(): String {
    return when (this) {
        WeightUnit.Pounds -> stringResource(id = R.string.lbs)
        WeightUnit.Kilograms -> stringResource(id = R.string.kg)
    }
}
