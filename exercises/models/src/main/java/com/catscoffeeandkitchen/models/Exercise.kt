package com.catscoffeeandkitchen.models

data class Exercise(
    val name: String,
    val id: Long = 0L,
    val aliases: List<String> = emptyList(),
    val musclesWorked: List<String> = emptyList(),
    val category: MuscleCategory? = null,
    val variations: List<Exercise> = emptyList(),
    val imageUrl: String? = null,
    val equipment: List<EquipmentType> = emptyList(),
    val amountOfSets: Int? = null,
    val stats: Stats? = null
)

