package com.catscoffeeandkitchen.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["eId"],
            childColumns = ["variationOf"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("name", unique = true)
    ]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val eId: Long,
    val name: String,
    val aliases: List<String> = emptyList(),
    val musclesWorked: List<String> = emptyList(),
    val category: String? = null,
    val variationOf: Long? = null,
    val userCreated: Boolean = true,
    val imageUrl: String? = null,
    val instructions: String? = null,
    val equipment: List<String> = emptyList()
)
