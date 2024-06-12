package com.catscoffeeandkitchen.models

data class Goal(
    val id: Long = 0L,
    val exercise: Exercise? = null,
    val group: ExerciseGroup? = null,
    val position: Int,
    val note: String? = null,
    val sets: Int? = 3,
    val setAmount: SetAmount = SetAmount.Fixed,
    val reps: Int? = 10,
    val minReps: Int? = 8,
    val maxReps: Int? = 12,
    val seconds: Int? = 30,
    val minSeconds: Int? = 30,
    val maxSeconds: Int? = 60,
    val weightInPounds: Float? = null,
    val weightInKilograms: Float? = null,
    val modifiers: List<SetModifier> = emptyList(),
    val equipment: List<EquipmentType>? = null,
    val repsInReserve: Int? = null,
    val perceivedExertion: Int? = null
)