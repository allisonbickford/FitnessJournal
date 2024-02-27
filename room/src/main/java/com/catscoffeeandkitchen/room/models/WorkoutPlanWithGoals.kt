package com.catscoffeeandkitchen.room.models

import androidx.room.Embedded
import androidx.room.Relation
import com.catscoffeeandkitchen.room.entities.GoalEntity
import com.catscoffeeandkitchen.room.entities.WorkoutPlanEntity


data class WorkoutPlanWithGoals(
    @Embedded val plan: WorkoutPlanEntity,
    @Relation(
        parentColumn = "wpId",
        entity = GoalEntity::class,
        entityColumn = "planId",
    )
    val goals: List<GoalEntity> = emptyList(),
)
