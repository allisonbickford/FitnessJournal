package com.catscoffeeandkitchen.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import com.catscoffeeandkitchen.room.models.ExerciseWithAmountPerformed
import com.catscoffeeandkitchen.room.models.ExerciseWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(exercises: List<ExerciseEntity>): List<Long>

    @Update
    suspend fun updateAll(exercises: List<ExerciseEntity>)

    @Update
    suspend fun update(exercises: ExerciseEntity)

    @Delete
    suspend fun delete(exercise: ExerciseEntity)

    @Query(
        """
        DELETE FROM ExerciseEntity
        WHERE NOT ExerciseEntity.userCreated
    """
    )
    suspend fun clearRemoteExercises()

    @Query(
        """
        SELECT *, COUNT(SetEntity.sId) AS amountPerformed
        FROM ExerciseEntity
        LEFT JOIN WorkoutEntryEntity ON WorkoutEntryEntity.entry_exercise_eId = ExerciseEntity.eId
        LEFT JOIN SetEntity ON SetEntity.entryId = WorkoutEntryEntity.weId
        GROUP BY ExerciseEntity.eId
        HAVING amountPerformed > 0
        ORDER BY amountPerformed DESC
    """
    )
    suspend fun getAllExercises(): List<ExerciseWithAmountPerformed>
    @Transaction
    @Query(
        """
        SELECT e1.*
        FROM ExerciseEntity as e1
        WHERE :search = ''
        OR (
            :search <> ''
            AND e1.name LIKE :search
        ) 
        OR (
            :muscle <> ''
            AND e1.musclesWorked LIKE :muscle
        )
        OR (
            :category <> ''
            AND e1.category LIKE :category
        )
    """
    )
    fun searchExercisesPaged(
        search: String,
        muscle: String,
        category: String
    ): PagingSource<Int, ExerciseEntity>


    @Transaction
    @Query(
        """
        SELECT e1.*
        FROM ExerciseEntity as e1
        WHERE :search = ''
        OR (
            :search <> ''
            AND e1.name LIKE :search
        ) 
        OR (
            :muscle <> ''
            AND e1.musclesWorked LIKE :muscle
        )
        OR (
            :category <> ''
            AND e1.category LIKE :category
        )
        LIMIT :limit
        OFFSET :offset
    """
    )
    fun searchExercises(
        search: String,
        muscle: String,
        category: String,
        limit: Int,
        offset: Int
    ): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT *
        FROM ExerciseEntity
        WHERE ExerciseEntity.name LIKE '%' || :exerciseName || '%'
        LIMIT 1
    """
    )
    fun searchExercisesByName(exerciseName: String): ExerciseEntity?

    @Query(
        """
        SELECT *
        FROM ExerciseEntity
        WHERE eId IN (:ids)
    """
    )
    suspend fun getExercisesByIds(ids: List<Long>): List<ExerciseEntity>

    @Query(
        """
        SELECT *
        FROM ExerciseEntity
        WHERE name IN (:names)
    """
    )
    suspend fun getExercisesByName(names: List<String>): List<ExerciseEntity>

    @Transaction
    @Query(
        """
        SELECT ExerciseEntity.name as exerciseName,
            COUNT(SetEntity.completedAt) AS amountPerformed,
            (
                SELECT COUNT(*)
                FROM ExerciseEntity 
                JOIN WorkoutEntryEntity ON WorkoutEntryEntity.entry_exercise_eId = ExerciseEntity.eId
                JOIN SetEntity ON SetEntity.entryId = WorkoutEntryEntity.weId
                WHERE name = :name
                    AND SetEntity.type = "Working"
                    AND SetEntity.completedAt BETWEEN :startOfWeek AND :currentTime
            ) as amountCompletedThisWeek,
            MAX(SetEntity.completedAt) as lastCompletedAt,
            MAX(SetEntity.weightInKilograms) as highestWeightInKilograms,
            MAX(SetEntity.weightInPounds) as highestWeightInPounds
        FROM ExerciseEntity
        JOIN WorkoutEntryEntity ON WorkoutEntryEntity.entry_exercise_eId = ExerciseEntity.eId
        JOIN SetEntity ON SetEntity.entryId = WorkoutEntryEntity.weId
        WHERE name = :name AND SetEntity.type = "Working"
        GROUP BY ExerciseEntity.eId
    """
    )
    suspend fun getExerciseWithStatsByName(name: String, startOfWeek: Long, currentTime: Long): ExerciseWithStats?

    @Query(
        """
        SELECT *
        FROM ExerciseEntity
        WHERE eId = :eId
    """
    )
    suspend fun getExerciseById(eId: Long): ExerciseEntity

    @Query(
        """
        SELECT *
        FROM ExerciseEntity
        WHERE ExerciseEntity.name=:name
        LIMIT 1
    """
    )
    suspend fun getExerciseByName(name: String): ExerciseEntity?

    @Transaction
    @Query(
        """
        SELECT *
        FROM ExerciseEntity
        WHERE ExerciseEntity.eId = :id
    """
    )
    fun get(id: Long): Flow<ExerciseEntity>

}
