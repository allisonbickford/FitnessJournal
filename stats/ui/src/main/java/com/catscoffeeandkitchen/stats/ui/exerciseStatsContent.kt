package com.catscoffeeandkitchen.stats.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.ui.charts.ExerciseStatsChart
import timber.log.Timber
import java.time.OffsetDateTime

fun LazyListScope.exerciseStatsContent(
    selectedExercise: Exercise,
    entries: List<WorkoutEntry>,
    unit: WeightUnit
) {
    item {
        Text(
            selectedExercise.name,
            style = MaterialTheme.typography.headlineMedium
        )
    }

    item {
        val statsData = entries
            .flatMap { it.sets }
            .groupBy { it.completedAt }
            .map { grouping ->
                val sortedGroup = grouping.value.sortedByDescending { set ->
                    set.repMaxInPounds
                }
                StatsData(
                    date = grouping.key ?: OffsetDateTime.now(),
                    bestSet = sortedGroup.first(),
                    repMax = (sortedGroup.first().repMaxInPounds).toFloat(),
                    totalVolume = grouping.value.maxOf { it.weightInPounds * it.reps },
                    highestWeight = grouping.value.maxOf { it.weightInPounds },
                    reps = grouping.value.maxOf { it.reps }.toFloat()
                )
            }

        Timber.d("*** statsData = ${statsData.joinToString { it.repMax.toString() }}")

        if (entries.isNotEmpty()) {
            ExerciseStatsChart(
                stats = ExerciseProgressStats(
                    exercise = selectedExercise,
                    sets = entries.flatMap { it.sets }
                ),
                unit = unit
            )
        } else {
            Text("No sets to graph.")
        }
    }
}
