package com.catscoffeeandkitchen.models

import java.time.DayOfWeek
import java.time.OffsetDateTime

data class WorkoutWeekStats(
    val since: OffsetDateTime? = null,
    val averageWorkoutsPerWeek: Float? = null,
    val mostCommonDays: List<DayOfWeek> = emptyList(),
    val mostCommonTimes: List<Int> = emptyList()
)