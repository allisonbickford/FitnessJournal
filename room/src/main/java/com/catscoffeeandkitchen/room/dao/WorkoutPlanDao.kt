package com.catscoffeeandkitchen.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.catscoffeeandkitchen.room.entities.WorkoutPlanEntity
import com.catscoffeeandkitchen.room.models.PlanWithGoals
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(workout: WorkoutPlanEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(workout: WorkoutPlanEntity): Int

    @Delete
    suspend fun delete(workout: WorkoutPlanEntity)

    @Transaction
    @Query(
        """
        SELECT *
        FROM WorkoutPlanEntity
        WHERE WorkoutPlanEntity.wpId = :planId
    """
    )
    fun get(planId: Long): Flow<PlanWithGoals>

    @Transaction
    @Query(
        """
        SELECT *
        FROM WorkoutPlanEntity
    """
    )
    fun getAll(): Flow<List<PlanWithGoals>>

    @Transaction
    @Query("""
        SELECT *
        FROM WorkoutPlanEntity
        WHERE NOT EXISTS (
            SELECT 1
            FROM WorkoutEntity
            WHERE WorkoutEntity.planId = wpId AND WorkoutEntity.completedAt IS NULL
        ) AND daysOfWeek LIKE '%' || :day || '%'
        ORDER BY WorkoutPlanEntity.addedAt DESC
        LIMIT 1
    """)
    fun getNextPlanWithDay(day: String): Flow<PlanWithGoals?>

    @Transaction
    @Query("""
        SELECT *
        FROM WorkoutPlanEntity
        INNER JOIN WorkoutEntity ON WorkoutEntity.planId = wpId
        WHERE WorkoutEntity.wId = :workoutId
    """)
    fun getForWorkout(workoutId: Long): Flow<PlanWithGoals?>
}
