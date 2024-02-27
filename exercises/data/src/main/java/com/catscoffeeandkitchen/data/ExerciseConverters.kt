package com.catscoffeeandkitchen.data

import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.models.SetModifier
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.models.Stats
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import com.catscoffeeandkitchen.room.entities.ExerciseGroupEntity
import com.catscoffeeandkitchen.room.entities.SetEntity

object ExerciseConverters {
    fun Exercise.toEntity(): ExerciseEntity {
        return ExerciseEntity(
            eId = id,
            name = name,
            aliases = aliases,
            musclesWorked = musclesWorked,
            category = category?.name,
            variationOf = null,
            userCreated = false,
            imageUrl = imageUrl,
            equipment = equipment.map { it.name }
        )
    }

    fun ExerciseEntity.toExercise(
        variations: List<Exercise> = emptyList(),
        amountOfSets: Int? = null,
        stats: Stats? = null
    ): Exercise {
        return Exercise(
            id = eId,
            name = name,
            aliases = aliases,
            musclesWorked = musclesWorked,
            category = category?.let { MuscleCategory.valueOf(it) },
            imageUrl = imageUrl,
            variations = variations,
            amountOfSets = amountOfSets,
            stats = stats
        )
    }


    fun ExerciseSet.toEntity(
        entryId: Long,
    ): SetEntity {
        return SetEntity(
            sId = id,
            entryId = entryId,
            reps = reps,
            weightInPounds = weightInPounds,
            weightInKilograms = weightInKilograms,
            repsInReserve = repsInReserve,
            perceivedExertion = perceivedExertion,
            setNumber = setNumber,
            completedAt = completedAt,
            type = type.name,
            seconds = seconds,
            modifier = modifiers?.map { it.name }
        )
    }

    fun SetEntity.toSet(): ExerciseSet {
        return ExerciseSet(
            id = sId,
            reps = reps,
            setNumber = setNumber,
            weightInPounds = weightInPounds,
            weightInKilograms = weightInKilograms,
            repsInReserve = repsInReserve,
            perceivedExertion = perceivedExertion,
            completedAt = completedAt,
            type = SetType.valueOf(type),
            seconds = seconds,
            modifiers = modifier?.map { SetModifier.valueOf(it) }
        )
    }

    fun ExerciseGroup.toEntity(): ExerciseGroupEntity {
        return ExerciseGroupEntity(
            gId = id,
            name = name
        )
    }

    fun ExerciseGroupEntity.toGroup(
        exercises: List<Exercise>
    ): ExerciseGroup {
        return ExerciseGroup(
            id = gId,
            name = name,
            exercises = exercises
        )
    }
}