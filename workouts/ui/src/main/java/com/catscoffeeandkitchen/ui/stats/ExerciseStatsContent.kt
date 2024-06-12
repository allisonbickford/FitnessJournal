package com.catscoffeeandkitchen.ui.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.exercises.youtube_player.YouTubeHorizontalPager
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseEntries
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.ExerciseDetailScreen
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.SimilarExercisesRow
import com.catscoffeeandkitchen.ui.abbreviation
import com.catscoffeeandkitchen.ui.charts.ExerciseStatsChart
import com.catscoffeeandkitchen.ui.detail.ExerciseFullWidthImage
import com.catscoffeeandkitchen.ui.detail.ExerciseInstructions
import com.catscoffeeandkitchen.ui.theme.Spacing
import com.catscoffeeandkitchen.ui.toCleanString

@Composable
fun ExerciseStatsContent(
    exerciseEntries: ExerciseEntries,
    unit: WeightUnit,
    similarExercises: List<Exercise> = emptyList()
) {
    var showFullContents by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = Spacing.Default)
    ) {
        if (exerciseEntries.entries.any { it.setsComplete }) {
            item {
                Text(
                    stringResource(R.string.last_completed),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(
                        vertical = Spacing.Half,
                        horizontal = Spacing.Default
                    )
                )

                HorizontalDivider()
            }

            items(exerciseEntries.entries
                .filter { it.setsComplete }
                .sortedByDescending { it.completedAt }
                .take(3)
            ) { entry ->
                Column(
                    modifier = Modifier.padding(
                        horizontal = Spacing.Default,
                        vertical = Spacing.Half
                    )
                ) {
                    val grouped = entry.sets.groupBy { it.reps to it.weight(unit) }

                    grouped.forEach { group ->
                        Text(
                            "${group.value.size}x${group.key.second.toCleanString()}" +
                                    "${unit.abbreviation()} for ${group.key.first} reps",
                            modifier = Modifier.padding(start = Spacing.Quarter)
                        )
                    }
                }

                HorizontalDivider()
            }

            item {
                ExerciseStatsChart(
                    stats = ExerciseProgressStats(
                        exercise = exerciseEntries.exercise,
                        sets = exerciseEntries.sets
                    ),
                    unit = unit
                )
            }
        }

        if (showFullContents) {
            item {
                ExerciseFullWidthImage(
                    imageUrl = exerciseEntries.exercise.imageUrl,
                    contentDescription = "Depiction of ${exerciseEntries.exercise.name}"
                )
            }

            item {
                ExerciseInstructions(instruction = exerciseEntries.exercise.instructions.orEmpty())
            }

            item {
                Text(
                    "Videos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        vertical = Spacing.Default,
                        horizontal = Spacing.Half
                    )
                )

                YouTubeHorizontalPager(
                    search = "${exerciseEntries.exercise.name} Exercise",
                    startWithVolumeOn = false
                )
            }

            if (similarExercises.isNotEmpty()) {
                item {
                    Text(
                        "Similar Exercises",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(
                            vertical = Spacing.Default,
                            horizontal = Spacing.Half
                        )
                    )

                    SimilarExercisesRow(
                        exercises = similarExercises
                    )
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.Default),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { showFullContents = true }) {
                        Text("Show More")

                        Spacer(Modifier.padding(start = Spacing.Half))

                        Icon(Icons.Default.KeyboardArrowDown, "expand")
                    }
                }
            }
        }
    }
}
