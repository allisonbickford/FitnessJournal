package com.catscoffeeandkitchen.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["wId"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["workoutId"])
    ]
)
data class WorkoutEntryEntity(
    @PrimaryKey(autoGenerate = true) val weId: Long,
    val position: Int,
    val workoutId: Long,
    @Embedded(prefix = "entry_exercise_") val exercise: ExerciseEntity?,
    @Embedded(prefix = "entry_group_") val group: ExerciseGroupEntity?
)
