package com.catscoffeeandkitchen.room.models

import androidx.room.Embedded
import androidx.room.Relation
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import com.catscoffeeandkitchen.room.entities.ExerciseGroupEntity
import com.catscoffeeandkitchen.room.entities.GoalEntity
import com.catscoffeeandkitchen.room.entities.SetEntity

data class GoalWithData(
    @Embedded val goal: GoalEntity,
    @Relation(
        parentColumn = "groupId",
        entity = ExerciseGroupEntity::class,
        entityColumn = "gId",
    )
    val group: GroupWithExercises?,
    @Relation(
        parentColumn = "exerciseId",
        entity = ExerciseEntity::class,
        entityColumn = "eId",
    )
    val exercise: ExerciseEntity?
)
