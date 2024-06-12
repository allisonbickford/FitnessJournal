package com.catscoffeeandkitchen.models

import java.time.OffsetDateTime

data class Workout(
    val id: Long,
    val addedAt: OffsetDateTime = OffsetDateTime.now(),
    val name: String = "New Workout",
    val note: String? = null,
    val completedAt: OffsetDateTime? = null,
    val planId: Long? = null,
    val entries: List<WorkoutEntry> = emptyList()
)