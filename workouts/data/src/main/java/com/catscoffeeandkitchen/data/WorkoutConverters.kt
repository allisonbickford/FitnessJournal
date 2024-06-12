package com.catscoffeeandkitchen.data

import com.catscoffeeandkitchen.data.ExerciseConverters.toEntity
import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.data.ExerciseConverters.toGroup
import com.catscoffeeandkitchen.data.WorkoutConverters.toEntity
import com.catscoffeeandkitchen.data.WorkoutConverters.toEntry
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.Workout
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.room.entities.GoalEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntryEntity

object WorkoutConverters {

    fun Workout.toEntity(): WorkoutEntity {
        return WorkoutEntity(
            wId = id,
            planId = planId,
            addedAt = addedAt,
            completedAt = completedAt,
            name = name,
            note = note,
        )
    }

    fun WorkoutEntity.toWorkout(
        usingEntries: List<WorkoutEntry>
    ): Workout {
        return Workout(
            id = wId,
            addedAt = addedAt,
            name = name,
            note = note,
            completedAt = completedAt,
            planId = planId,
            entries = usingEntries
        )
    }

    fun WorkoutEntry.toEntity(workoutId: Long, planId: Long? = null): WorkoutEntryEntity {
        return WorkoutEntryEntity(
            weId = id,
            position = position,
            workoutId = workoutId,
            exercise = exercise?.toEntity(),
            group = group?.toEntity()
        )
    }

    fun WorkoutEntryEntity.toEntry(usingSets: List<ExerciseSet>): WorkoutEntry {
        return WorkoutEntry(
            id = weId,
            position = position,
            group = group?.toGroup(
                exercises = emptyList()
            ),
            exercise = exercise?.toExercise(),
            sets = usingSets
        )
    }
}
