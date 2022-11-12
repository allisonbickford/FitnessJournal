package com.catscoffeeandkitchen.fitnessjournal.ui.workouts.list

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.domain.models.Workout
import com.catscoffeeandkitchen.fitnessjournal.ui.components.FitnessJournalCard
import com.catscoffeeandkitchen.fitnessjournal.ui.navigation.FitnessJournalScreen
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkoutSummaryCard(
    workout: Workout,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
) {
    FitnessJournalCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onTap() },
                onLongClick = { onLongPress() },
            ),
        columnPaddingVertical = 4.dp
    ) {
        Text(workout.name, style = MaterialTheme.typography.headlineMedium)

        if (workout.completedAt == null) {

            Surface(
                modifier = Modifier.padding(vertical = 4.dp),
                shape = SuggestionChipDefaults.shape,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                ),
                tonalElevation = 4.dp
            ) {
                Text(
                    "In Progress",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        when (val completed = workout.completedAt) {
            null -> Text(
                "added " + DateUtils.getRelativeTimeSpanString(
                    workout.addedAt.toInstant().toEpochMilli(),
                    OffsetDateTime.now().toInstant().toEpochMilli(),
                    DateUtils.DAY_IN_MILLIS
                ).toString().lowercase(),
                style = MaterialTheme.typography.labelMedium
            )
            else -> Text(
                "finished " + DateUtils.getRelativeTimeSpanString(
                    completed.toInstant().toEpochMilli(),
                    OffsetDateTime.now().toInstant().toEpochMilli(),
                    DateUtils.DAY_IN_MILLIS
                ).toString().lowercase(),
                style = MaterialTheme.typography.labelMedium
            )
        }

        when (val plan = workout.plan) {
            null -> {}
            else -> {
                Text("plan: ${plan.name}")
                Text("${plan.exercises.size} exercises")
            }
        }

        if (workout.note?.isNotEmpty() == true) {
            Text(
                workout.note!!,
                style = MaterialTheme.typography.labelLarge,
            )
        }

//            Text(
//                workout.expectedSets.size.toString() + " sets",
//                style = MaterialTheme.typography.labelLarge
//            )
    }
}

@Preview
@Composable
fun WorkoutCardPreview() {
    WorkoutSummaryCard(workout = Workout(
        name = "Best Workout Ever",
        addedAt = OffsetDateTime.now().minusDays(30L),
        completedAt = OffsetDateTime.now().minusDays(3),
        note = "A good workout for a nice burn"
    ))
}
