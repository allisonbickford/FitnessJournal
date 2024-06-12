package com.catscoffeeandkitchen.models

import java.time.OffsetDateTime
import kotlin.math.roundToInt

data class WorkoutEntry(
    val id: Long = 0L,
    val position: Int,
    val group: ExerciseGroup? = null,
    val exercise: Exercise? = null,
    val sets: List<ExerciseSet> = emptyList()
) {
    val name: String
        get() = exercise?.name ?: "Exercise"

    val setsComplete: Boolean
        get() = sets.all { it.isComplete }

    val completedAt: OffsetDateTime?
        get() = sets.filter { it.isComplete }.maxOfOrNull { it.completedAt!! }

    val isSingleSide: Boolean
        get() = sets.any { it.modifiers.orEmpty().contains(SetModifier.SingleSide) }

    val isTimed: Boolean
        get() = sets.any { it.modifiers.orEmpty().contains(SetModifier.Timed) }

    val averageReps: Int
        get() = sets.map { it.reps }
            .average()
            .takeUnless { avg -> avg.isNaN() }
            ?.roundToInt()
            ?: 0

    override fun toString(): String {
        return "Workout Entry(\n position = $position " +
                "\n exercise = $exercise " +
                "\n group = $group " +
                "\n sets = $sets " +
                "\n)"
    }
}