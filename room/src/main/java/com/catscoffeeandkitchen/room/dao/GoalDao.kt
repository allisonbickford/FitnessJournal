package com.catscoffeeandkitchen.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.catscoffeeandkitchen.room.entities.GoalEntity
import com.catscoffeeandkitchen.room.models.GoalWithData
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goals: List<GoalEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(goal: GoalEntity): Int

    @Query("""
        DELETE FROM GoalEntity
        WHERE gId = :id
    """)
    suspend fun delete(id: Long)

    @Transaction
    @Query(
        """
        SELECT *
        FROM GoalEntity
        WHERE GoalEntity.planId = :planId
    """
    )
    fun getInPlan(planId: Long): Flow<List<GoalEntity>>


    @Transaction
    @Query(
        """
        SELECT *
        FROM GoalEntity
        WHERE GoalEntity.planId = :planId AND position = :position
    """
    )
    suspend fun getInPlanAtPosition(planId: Long, position: Int): GoalEntity?

    @Transaction
    @Query(
        """
        SELECT *
        FROM GoalEntity
        INNER JOIN WorkoutEntity ON GoalEntity.planId = WorkoutEntity.planId
        WHERE WorkoutEntity.wId = :workoutId
    """
    )
    fun getWithDataInWorkout(workoutId: Long): Flow<List<GoalWithData>>

}
