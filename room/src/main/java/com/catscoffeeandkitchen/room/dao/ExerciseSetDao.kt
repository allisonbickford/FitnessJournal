package com.catscoffeeandkitchen.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import com.catscoffeeandkitchen.room.entities.SetEntity

@Dao
interface ExerciseSetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exerciseSet: SetEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(sets: List<SetEntity>): List<Long>

    @Update
    suspend fun update(exerciseSet: SetEntity)

    @Update
    suspend fun updateAll(exerciseSet: List<SetEntity>)

    @Query(
        """
        DELETE FROM SetEntity
        WHERE sId = :setId
    """
    )
    suspend fun delete(setId: Long)

    @Query(
        """
        DELETE FROM SetEntity
        WHERE sId IN (:ids)
    """
    )
    suspend fun deleteAll(ids: List<Long>)

    @Query("""
        SELECT *
        FROM SetEntity
        WHERE sId = :id
    """)
    suspend fun getSet(id: Long): SetEntity

    @Query(
        """
        SELECT *
        FROM SetEntity
        WHERE sId IN (:ids)
    """
    )
    suspend fun getSetsByIds(ids: List<Long>): List<SetEntity>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT *
        FROM SetEntity
        INNER JOIN WorkoutEntryEntity ON SetEntity.entryId = WorkoutEntryEntity.weId
        WHERE WorkoutEntryEntity.workoutId = :workoutId
    """
    )
    suspend fun getSetsInWorkout(workoutId: Long): List<SetEntity>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query(
        """
        SELECT *
        FROM SetEntity
        INNER JOIN WorkoutEntryEntity ON SetEntity.entryId = WorkoutEntryEntity.weId
        WHERE WorkoutEntryEntity.entry_exercise_eId = :eId 
            AND SetEntity.completedAt IS NOT NULL
    """
    )
    suspend fun getAllCompletedSetsForExercise(eId: Long): List<SetEntity>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT *
        FROM SetEntity
        INNER JOIN WorkoutEntryEntity ON SetEntity.entryId = WorkoutEntryEntity.weId
        WHERE WorkoutEntryEntity.entry_exercise_eId = :exerciseId 
                AND SetEntity.completedAt IS NOT NULL
        ORDER BY SetEntity.completedAt DESC
        LIMIT 1
    """
    )
    suspend fun getLastCompletedSet(exerciseId: Long): SetEntity?

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT *
        FROM SetEntity
        INNER JOIN WorkoutEntryEntity ON SetEntity.entryId = WorkoutEntryEntity.weId
        WHERE WorkoutEntryEntity.entry_exercise_eId = :exerciseId 
            AND SetEntity.type = "Working"
        ORDER BY SetEntity.sId DESC
        LIMIT 1
    """
    )
    suspend fun getLastSet(exerciseId: Long): SetEntity?

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT *
        FROM SetEntity
        INNER JOIN WorkoutEntryEntity ON SetEntity.entryId = WorkoutEntryEntity.weId
        WHERE entry_exercise_eId = :exerciseId AND WorkoutEntryEntity.workoutId = :workoutId
        ORDER BY setNumber DESC
        LIMIT 1
    """
    )
    suspend fun getLastSetOfExerciseInWorkout(exerciseId: Long, workoutId: Long): SetEntity?

    @Query("""
        SELECT *
        FROM SetEntity
        WHERE entryId in (:entryIds)
    """)
    suspend fun getSetsByEntryIds(entryIds: List<Long>): List<SetEntity>
}
