package com.catscoffeeandkitchen.room.models

import androidx.room.Embedded
import androidx.room.Relation
import com.catscoffeeandkitchen.room.entities.WorkoutEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntryEntity

data class WorkoutWithSetsAndExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "wId",
        entity = WorkoutEntryEntity::class,
        entityColumn = "workoutId"
    )
    val sets: List<EntryWithSets> = emptyList(),
)
