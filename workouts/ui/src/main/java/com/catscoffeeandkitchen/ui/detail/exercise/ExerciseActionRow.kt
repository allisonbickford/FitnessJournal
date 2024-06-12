package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme

@Composable
fun ExerciseActionRow(
    entry: WorkoutEntry,
    unit: WeightUnit,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onExerciseAction: (ExerciseAction) -> Unit,
    onNavigationAction: (ExerciseNavigationAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RowItem(
            icon = Icons.Default.Add,
            label = "Warmup",
            onClick = { onExerciseAction(ExerciseAction.AddWarmupSets(entry, unit)) }
        )

        RowItem(
            icon = Icons.Default.Delete,
            label = "Delete",
            onClick = { onExerciseAction(ExerciseAction.RemoveEntry(entry)) }
        )

        RowItem(
            icon = Icons.Filled.Refresh,
            label = "Swap",
            onClick = {
                onNavigationAction(ExerciseNavigationAction.SwapExerciseAt(entry.position))
            }
        )

        RowItem(
            icon = Icons.Default.KeyboardArrowUp,
            label = "Move Up",
            enabled = canMoveUp,
            onClick = { onExerciseAction(ExerciseAction.MoveEntryTo(entry, entry.position - 1)) }
        )

        RowItem(
            icon = Icons.Default.KeyboardArrowDown,
            label = "Move Down",
            enabled = canMoveDown,
            onClick = { onExerciseAction(ExerciseAction.MoveEntryTo(entry, entry.position + 1)) }
        )
    }
}

@Composable
private fun RowItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Icon(
                icon,
                contentDescription = label
            )
        }

        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) Color.Unspecified
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
private fun ExerciseActionRowPreview() {
    LiftingLogTheme {
        Card {
            ExerciseActionRow(
                entry = WorkoutEntry(id = 1, position = 1),
                unit = WeightUnit.Pounds,
                canMoveUp = true,
                canMoveDown = true,
                onExerciseAction = { },
                onNavigationAction = { }
            )
        }
    }
}
