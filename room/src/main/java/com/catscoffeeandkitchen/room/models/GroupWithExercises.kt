package com.catscoffeeandkitchen.room.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import com.catscoffeeandkitchen.room.entities.ExerciseGroupEntity
import com.catscoffeeandkitchen.room.entities.GroupExerciseXRef

data class GroupWithExercises(
    @Embedded val group: ExerciseGroupEntity,
    @Relation(
        parentColumn = "gId",
        entity = ExerciseEntity::class,
        entityColumn = "eId",
        associateBy = Junction(
            value = GroupExerciseXRef::class,
            parentColumn = "groupId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<ExerciseEntity> = emptyList(),
)
