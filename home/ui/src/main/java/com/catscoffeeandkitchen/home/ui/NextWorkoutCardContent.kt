package com.catscoffeeandkitchen.home.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.WorkoutPlan
import com.catscoffeeandkitchen.ui.components.LLButton

@Composable
fun NextWorkoutCardContent(
    state: HomeViewModel.ResultState<WorkoutPlan>,
    createNewWorkout: (planId: Long) -> Unit
) {
    Text(
        stringResource(R.string.next_workout),
        style = MaterialTheme.typography.titleLarge
    )

    if (state.isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    } else if (state.error != null) {
        Text(
            state.error.localizedMessage
                ?: stringResource(R.string.next_workout_error_message),
            style = MaterialTheme.typography.titleLarge
        )
    } else if (state.result != null) {
        val plan = state.result

        Text(plan.name, style = MaterialTheme.typography.titleLarge)
        plan.note?.takeIf { it.isNotEmpty() }?.let { note ->
            Text(note, style = MaterialTheme.typography.bodyMedium)
        }

        plan.goals.forEach { entry ->
            Text("${entry.sets}x${entry.reps} " +
                    "${entry.group?.name ?: entry.exercise?.name}",
                style = MaterialTheme.typography.labelMedium
            )
        }

        LLButton(
            onClick = {
                createNewWorkout(plan.id)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.start_workout))
        }
    } else {
        Text(
            stringResource(R.string.no_workouts_planned),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
