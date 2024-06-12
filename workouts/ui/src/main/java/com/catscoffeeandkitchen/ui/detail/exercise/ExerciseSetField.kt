package com.catscoffeeandkitchen.ui.detail.exercise

import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.SetModifier
import com.catscoffeeandkitchen.models.SetType
import java.time.OffsetDateTime

sealed class ExerciseSetField(val value: Any?) {
    class Reps(value: Int): ExerciseSetField(value)
    class WeightInPounds(value: Float): ExerciseSetField(value)
    class WeightInKilograms(value: Float): ExerciseSetField(value)
    class RepsInReserve(value: Int): ExerciseSetField(value)
    class PerceivedExertion(value: Int): ExerciseSetField(value)
    class Type(value: SetType): ExerciseSetField(value)
    class ExerciseModifier(value: SetModifier?): ExerciseSetField(value)
    class Complete(value: OffsetDateTime?): ExerciseSetField(value)

    @Suppress("MagicNumber")
    fun copySetWithNewValue(
        set: ExerciseSet,
    ): ExerciseSet {
        return when (this) {
            is Reps -> {
                set.copy(reps = value as Int)
            }
            is WeightInPounds -> {
                set.copy(
                    weightInPounds = value as Float,
                    weightInKilograms = (value * 0.4535924f)
                )
            }
            is WeightInKilograms -> {
                set.copy(
                    weightInKilograms = value as Float,
                    weightInPounds = (value * 2.204623f)
                )
            }
            is RepsInReserve -> {
                set.copy(repsInReserve = value as Int)
            }
            is PerceivedExertion -> {
                set.copy(perceivedExertion = value as Int)
            }
            is Complete -> {
                set.copy(completedAt = value as OffsetDateTime?)
            }
            is ExerciseModifier -> set.copy(modifiers = (value as? List<SetModifier>) ?: emptyList())
            is Type -> set.copy(type = value as SetType)
        }
    }
}
