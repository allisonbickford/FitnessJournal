package com.catscoffeeandkitchen.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlanEntity::class,
            parentColumns = ["wpId"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["eId"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseGroupEntity::class,
            parentColumns = ["gId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["planId"])
    ]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val gId: Long,
    val exerciseId: Long? = null,
    val groupId: Long? = null,
    val planId: Long,
    val position: Int,
    val note: String?,
    val reps: Int? = 10,
    val sets: Int? = 3,
    val setAmount: String,
    val minReps: Int? = 8,
    val maxReps: Int? = 12,
    val seconds: Int? = 30,
    val minSeconds: Int? = 30,
    val maxSeconds: Int? = 45,
    val weightInPounds: Float? = null,
    val weightInKilograms: Float? = null,
    val modifiers: List<String>? = null,
    val equipment: List<String>? = null,
    val repsInReserve: Int? = null,
    val perceivedExertion: Int? = null
)
