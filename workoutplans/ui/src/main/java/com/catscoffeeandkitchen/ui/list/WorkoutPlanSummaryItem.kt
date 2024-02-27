package com.catscoffeeandkitchen.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.WorkoutPlan
import com.catscoffeeandkitchen.workoutplans.ui.R
import java.time.OffsetDateTime

@Composable
fun WorkoutPlanSummaryItem(
    workout: WorkoutPlan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(
                workout.name,
                style = MaterialTheme.typography.titleLarge
            )

            workout.note?.takeIf { it.isNotEmpty() }?.let { note ->
                Text(
                    note,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Text(
                pluralStringResource(
                    R.plurals.x_exercises,
                    count = workout.goals.size,
                    workout.goals.size
                ),
                style = MaterialTheme.typography.titleMedium
            )

            val musclesWorkedDescriptor = MusclesWorkedDescriptor(workout.goals)

            if (musclesWorkedDescriptor.mostCommonMuscleWorked.trim().isNotEmpty()) {
                Text(
                    stringResource(R.string.x_focused, musclesWorkedDescriptor.mostCommonMuscleWorked),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (musclesWorkedDescriptor.compoundMovements.isNotEmpty()) {
                Text(
                    stringResource(R.string.includes_x, musclesWorkedDescriptor.compoundMovements),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            workout.goals.forEach { goal ->
                val exerciseName = goal.exercise?.name
                    ?: goal.group?.name
                    ?: goal.note?.ifEmpty { " Unknown Exercise" }
                Text(
                    "${goal.sets}x${goal.reps} $exerciseName",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun WorkoutCardPreview() {
    WorkoutPlanSummaryItem(
        workout = WorkoutPlan(
            id = 1L,
            name = "Best Workout Ever",
            addedAt = OffsetDateTime.now().minusDays(30L),
            note = "A good workout for a nice burn"
        ),
        onClick = { }
    )
}
