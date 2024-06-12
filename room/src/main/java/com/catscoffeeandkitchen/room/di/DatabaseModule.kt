package com.catscoffeeandkitchen.room.di

import android.content.Context
import androidx.room.Room
import com.catscoffeeandkitchen.room.LiftingLogDatabase
import com.catscoffeeandkitchen.room.dao.EntryDao
import com.catscoffeeandkitchen.room.dao.ExerciseDao
import com.catscoffeeandkitchen.room.dao.ExerciseGroupDao
import com.catscoffeeandkitchen.room.dao.ExerciseSetDao
import com.catscoffeeandkitchen.room.dao.GoalDao
import com.catscoffeeandkitchen.room.dao.WorkoutDao
import com.catscoffeeandkitchen.room.dao.WorkoutPlanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
    ): LiftingLogDatabase {
        return Room.databaseBuilder(
            appContext,
            LiftingLogDatabase::class.java,
            "LiftingLogDatabase"
        )
            .build()
    }

    @Provides
    fun provideExerciseDao(database: LiftingLogDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    fun provideExerciseGroupDao(database: LiftingLogDatabase): ExerciseGroupDao {
        return database.exerciseGroupDao()
    }

    @Provides
    fun provideExerciseSetDao(database: LiftingLogDatabase): ExerciseSetDao {
        return database.exerciseSetDao()
    }

    @Provides
    fun provideWorkoutDao(database: LiftingLogDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideWorkoutPlanDao(database: LiftingLogDatabase): WorkoutPlanDao {
        return database.workoutPlanDao()
    }

    @Provides
    fun provideGoalDao(database: LiftingLogDatabase): GoalDao {
        return database.goalDao()
    }

    @Provides
    fun provideEntryDao(database: LiftingLogDatabase): EntryDao {
        return database.entryDao( )
    }
}
