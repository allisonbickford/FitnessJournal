package com.catscoffeeandkitchen.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.catscoffeeandkitchen.room.dao.EntryDao
import com.catscoffeeandkitchen.room.dao.ExerciseDao
import com.catscoffeeandkitchen.room.dao.ExerciseGroupDao
import com.catscoffeeandkitchen.room.dao.ExerciseSetDao
import com.catscoffeeandkitchen.room.dao.GoalDao
import com.catscoffeeandkitchen.room.dao.WorkoutDao
import com.catscoffeeandkitchen.room.dao.WorkoutPlanDao
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import com.catscoffeeandkitchen.room.entities.ExerciseGroupEntity
import com.catscoffeeandkitchen.room.entities.GroupExerciseXRef
import com.catscoffeeandkitchen.room.entities.SetEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntity
import com.catscoffeeandkitchen.room.entities.WorkoutPlanEntity
import com.catscoffeeandkitchen.room.entities.GoalEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntryEntity

@Database(
    entities = [
        ExerciseEntity::class,
        ExerciseGroupEntity::class,
        GroupExerciseXRef::class,
        SetEntity::class,
        WorkoutEntity::class,
        WorkoutPlanEntity::class,
        GoalEntity::class,
        WorkoutEntryEntity::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@TypeConverters(Converters::class)
abstract class LiftingLogDatabase: RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao
    abstract fun exerciseGroupDao(): ExerciseGroupDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun goalDao(): GoalDao
    abstract fun entryDao(): EntryDao
}
