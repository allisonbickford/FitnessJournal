package com.catscoffeeandkitchen.data

import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.SetAmount
import com.catscoffeeandkitchen.models.SetModifier
import com.catscoffeeandkitchen.models.WorkoutPlan
import com.catscoffeeandkitchen.room.entities.GoalEntity
import com.catscoffeeandkitchen.room.entities.WorkoutPlanEntity
import java.time.DayOfWeek

object WorkoutPlanConverters {
    fun WorkoutPlan.toEntity(): WorkoutPlanEntity {
        return WorkoutPlanEntity(
            wpId = id,
            addedAt = addedAt,
            name = name,
            note = note,
            daysOfWeek = daysOfWeek.map { it.name }
        )
    }

    fun WorkoutPlanEntity.toPlan(
        usingGoals: List<Goal>
    ): WorkoutPlan {
        return WorkoutPlan(
            id = wpId,
            addedAt = addedAt,
            name = name,
            note = note,
            goals = usingGoals,
            daysOfWeek = daysOfWeek.map { DayOfWeek.valueOf(it) }
        )
    }

    fun Goal.toEntity(planId: Long): GoalEntity {
        return GoalEntity(
            gId = id,
            exerciseId = exercise?.id,
            groupId = group?.id,
            planId = planId,
            position = position,
            note = note,
            sets = sets,
            setAmount = setAmount.name,
            reps = reps,
            minReps = minReps,
            maxReps = maxReps,
            seconds = seconds,
            minSeconds = minSeconds,
            maxSeconds = maxSeconds,
            weightInPounds = weightInPounds,
            weightInKilograms = weightInKilograms,
            modifiers = modifiers.map { it.name },
            equipment = equipment?.map { it.name },
            repsInReserve = repsInReserve,
            perceivedExertion = perceivedExertion
        )
    }

    fun GoalEntity.toGoal(
        usingGroup: ExerciseGroup? = null,
        usingExercise: Exercise? = null
    ): Goal {
        return Goal(
            id = gId,
            exercise = usingExercise,
            group = usingGroup,
            position = position,
            note = note,
            sets = sets,
            setAmount = SetAmount.valueOf(setAmount),
            reps = reps,
            minReps = minReps,
            maxReps = maxReps,
            seconds = seconds,
            minSeconds = minSeconds,
            maxSeconds = maxSeconds,
            weightInPounds = weightInPounds,
            weightInKilograms = weightInKilograms,
            modifiers = modifiers.orEmpty().map { SetModifier.valueOf(it) },
            equipment = equipment?.map { EquipmentType.valueOf(it) },
            repsInReserve = repsInReserve,
            perceivedExertion = perceivedExertion
        )
    }
}
