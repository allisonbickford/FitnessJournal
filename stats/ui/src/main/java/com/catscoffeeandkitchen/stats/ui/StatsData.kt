package com.catscoffeeandkitchen.stats.ui

import com.catscoffeeandkitchen.models.ExerciseSet
import java.time.OffsetDateTime

data class StatsData(
    val date: OffsetDateTime,
    val bestSet: ExerciseSet,
    val repMax: Float,
    val totalVolume: Float,
    val highestWeight: Float,
    val reps: Float,
)
