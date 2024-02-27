package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.ui.theme.Spacing


@Composable
fun ReadOnlyExerciseCardContent(
    entry: WorkoutEntry,
    unit: WeightUnit,
    modifier: Modifier = Modifier
) {
    if (entry.isSingleSide) {
        Surface(
            modifier = Modifier.padding(bottom = 4.dp),
            shape = SuggestionChipDefaults.shape,
            tonalElevation = 4.dp
        ) {
            Text(
                "Single Arm/Leg",
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

    Column(
        modifier = modifier
            .padding(
                start = Spacing.Default,
                end = Spacing.Default,
                bottom = Spacing.Default
            )
    ) {
        entry.sets.forEach { set ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${set.weight(unit)} ${unit.name.lowercase()}",
                    modifier = Modifier.weight(1f)
                )

                Text(
                    "${set.reps} reps",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
