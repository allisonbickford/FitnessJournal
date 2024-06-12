package com.catscoffeeandkitchen.ui.detail.exercise

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import java.time.Duration
import java.time.OffsetDateTime

@Composable
fun ExerciseGroupCard(
    group: ExerciseGroup,
    editGroup: () -> Unit,
    modifier: Modifier = Modifier,
    onExerciseSelected: (Exercise) -> Unit = {},
) {
    var showExtrasDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(group.name.orEmpty().ifEmpty { "Select from group" },
                style = MaterialTheme.typography.headlineSmall)

            Box(modifier = Modifier.weight(1f)) {
                IconButton(
                    onClick = { showExtrasDropdown = !showExtrasDropdown },
                ) {
                    Icon(Icons.Default.MoreVert, "more exercise options")
                }
            }

            DropdownMenu(
                expanded = showExtrasDropdown,
                onDismissRequest = { showExtrasDropdown = false }
            ) {
                DropdownMenuItem(
                    text = { Text("edit group") },
                    onClick = {
                        editGroup()
                    })
            }
        }

        Column(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            group.exercises.forEach { exercise ->
                GroupExerciseItem(exercise, onSelect = onExerciseSelected)
            }
        }

    }
}

@Composable
fun GroupExerciseItem(
    exercise: Exercise,
    onSelect: (Exercise) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp)
            .clickable { onSelect(exercise) }
    ) {
        Text(exercise.name, style = MaterialTheme.typography.titleMedium)
        if (exercise.stats != null) {
            val completed = exercise.stats?.lastCompletedAt
            val completedAmount = exercise.stats?.amountCompleted
            val completedAmountThisWeek = exercise.stats?.amountCompletedThisWeek

            if (completed != null) {
                val formattedDate = when {
                    Duration.between(completed, OffsetDateTime.now()).toDays() < 2 ->
                        DateUtils.getRelativeTimeSpanString(
                            completed.toInstant().toEpochMilli(),
                            OffsetDateTime.now().toInstant().toEpochMilli(),
                            DateUtils.DAY_IN_MILLIS
                        ).toString().lowercase()
                    else -> completed.dayOfWeek.name.lowercase()
                }
                Text(
                    "last completed $formattedDate",
                    style = MaterialTheme.typography.labelMedium
                )
            } else {
                Text(
                    "Never completed",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            if (completedAmountThisWeek != null && completedAmountThisWeek > 0) {
                Text("$completedAmountThisWeek " +
                        "${if (completedAmountThisWeek == 1) "set" else "sets"} " +
                        "this week",
                    style = MaterialTheme.typography.labelMedium
                )
            } else if (completedAmount != null && completedAmount > 0) {
                Text(
                    "completed ${exercise.stats?.amountCompleted} sets total, none this week",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        } else {
            Text("No stats available.")
        }
    }
}
