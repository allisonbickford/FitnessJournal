package com.catscoffeeandkitchen.models

import java.time.OffsetDateTime

data class ExerciseSet(
    val id: Long,
    val reps: Int,
    val setNumber: Int = 1,
    val weightInPounds: Float = 0f,
    val weightInKilograms: Float = 0f,
    val repsInReserve: Int = 0,
    val perceivedExertion: Int = 0,
    val completedAt: OffsetDateTime? = null,
    val type: SetType = SetType.Working,
    val seconds: Int = 0,
    val modifiers: List<SetModifier>? = null,
) {
    val isComplete: Boolean
        get() = completedAt != null

    @Suppress("MagicNumber")
    val repMaxInPounds: Double
        get() = weightInPounds / (1.0278 - 0.0278 * reps)

    @Suppress("MagicNumber")
    val repMaxInKilograms: Double
        get() = weightInKilograms / (1.0278 - 0.0278 * reps)

    fun repMax(unit: WeightUnit): Double = when (unit) {
        WeightUnit.Pounds -> repMaxInPounds
        WeightUnit.Kilograms -> repMaxInKilograms
    }

    fun weight(unit: WeightUnit): Float {
        return when (unit) {
            WeightUnit.Pounds -> weightInPounds
            WeightUnit.Kilograms -> weightInKilograms
        }
    }
}