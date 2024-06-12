package com.catscoffeeandkitchen.room.models

import androidx.room.Embedded
import com.catscoffeeandkitchen.room.entities.ExerciseEntity

data class ExerciseWithAmountPerformed(
    @Embedded val exercise: ExerciseEntity,
    val amountPerformed: Int
)
