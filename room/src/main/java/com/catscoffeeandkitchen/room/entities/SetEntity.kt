package com.catscoffeeandkitchen.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntryEntity::class,
            parentColumns = ["weId"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("entryId"),
    ]
)
data class SetEntity(
    @PrimaryKey(autoGenerate = true) val sId: Long,
    val entryId: Long,
    val reps: Int = 10,
    val weightInPounds: Float = 0f,
    val weightInKilograms: Float = 0f,
    val repsInReserve: Int = 0,
    val perceivedExertion: Int = 0,
    val setNumber: Int = 1,
    val completedAt: OffsetDateTime? = null,
    val type: String,
    val seconds: Int = 0,
    val modifier: List<String>? = null
)
