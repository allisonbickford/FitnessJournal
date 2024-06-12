package com.catscoffeeandkitchen.models

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ExerciseEntries(
    val exercise: Exercise,
    val entries: List<WorkoutEntry> = emptyList()
) {
    val sets = entries.flatMap { it.sets }

    val bestSet: ExerciseSet?
        get() = sets.maxByOrNull { it.weightInPounds / (1.0278 - 0.0278 * it.reps) }

    val worstSet: ExerciseSet?
        get() = sets.minByOrNull { it.weightInPounds / (1.0278 - 0.0278 * it.reps) }

    val earliestSetCompletedAt: OffsetDateTime?
        get() = sets.minOfOrNull { it.completedAt ?: OffsetDateTime.now() }

    val latestSetCompletedAt: OffsetDateTime?
        get() = sets.maxOfOrNull { it.completedAt ?: OffsetDateTime.now() }

    val timePeriodInSeconds: Long
        get() = (latestSetCompletedAt ?: OffsetDateTime.now()).toEpochSecond() -
                (earliestSetCompletedAt ?: OffsetDateTime.now()).toEpochSecond()

    val chronoUnit: ChronoUnit
        get() {
            return when {
                timePeriodInSeconds < 30.toDuration(DurationUnit.DAYS)
                    .inWholeSeconds -> ChronoUnit.DAYS
                timePeriodInSeconds < 62.toDuration(DurationUnit.DAYS)
                    .inWholeSeconds -> ChronoUnit.WEEKS
                else -> ChronoUnit.MONTHS
            }
        }

}
