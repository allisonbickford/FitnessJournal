package com.catscoffeeandkitchen.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExerciseGroupEntity(
    @PrimaryKey(autoGenerate = true) val gId: Long,
    val name: String? = null
)
