package com.catscoffeeandkitchen.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import com.catscoffeeandkitchen.room.entities.WorkoutEntryEntity
import com.catscoffeeandkitchen.room.models.EntryWithSets
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: WorkoutEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<WorkoutEntryEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entry: WorkoutEntryEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(entries: List<WorkoutEntryEntity>)
    @Query("""
        DELETE FROM WorkoutEntryEntity
        WHERE weId = :id
    """)
    suspend fun delete(id: Long)

    @Query(
        """
        SELECT *
        FROM WorkoutEntryEntity
        WHERE WorkoutEntryEntity.workoutId = :workoutId
    """
    )
    suspend fun getInWorkout(workoutId: Long): List<WorkoutEntryEntity>

    @Query(
        """
        SELECT *
        FROM WorkoutEntryEntity
        WHERE WorkoutEntryEntity.workoutId = :workoutId AND position = :position
    """
    )
    suspend fun getInWorkoutAtPosition(workoutId: Long, position: Int): WorkoutEntryEntity?

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("""
        SELECT *
        FROM WorkoutEntryEntity
        JOIN SetEntity on SetEntity.entryId = weId
        WHERE SetEntity.completedAt IS NOT NULL
        ORDER BY SetEntity.completedAt DESC
        LIMIT :amount
    """)
    fun getLatest(amount: Int): Flow<List<WorkoutEntryEntity>>

    @Query(
        """
            SELECT COUNT(*)
            FROM WorkoutEntryEntity
            WHERE WorkoutEntryEntity.workoutId = :workoutId
        """
    )
    suspend fun countInWorkout(workoutId: Long): Int

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("""
        SELECT e1.*,
        (   
            SELECT MAX(sets_for_reps.reps)
            FROM SetEntity as sets_for_reps
            INNER JOIN WorkoutEntryEntity as entries_for_reps
            ON sets_for_reps.entryId = entries_for_reps.weId
            WHERE entries_for_reps.workoutId <> :workoutId
                AND entries_for_reps.entry_exercise_eId = e1.entry_exercise_eId
                AND sets_for_reps.completedAt IS NOT NULL
            GROUP BY entries_for_reps.weId
        ) as max_reps,
        (   
            SELECT COUNT(*)
            FROM SetEntity as sets_count
            INNER JOIN WorkoutEntryEntity as entries_count
            ON sets_count.entryId = entries_count.weId
            WHERE entries_count.workoutId <> :workoutId
                AND entries_count.entry_exercise_eId = e1.entry_exercise_eId
                AND sets_count.completedAt IS NOT NULL
            GROUP BY entries_count.weId
        ) as set_count
        FROM WorkoutEntryEntity as e1
        INNER JOIN SetEntity as s1 ON e1.weId = s1.entryId
        WHERE e1.workoutId = :workoutId
        GROUP BY e1.entry_exercise_name
        HAVING max_reps IS NOT NULL AND set_count IS NOT NULL
        ORDER BY abs(s1.reps - max_reps), abs(COUNT(s1.sId) - set_count)
    """)
    fun getComparableEntriesForWorkout(workoutId: Long): Flow<List<WorkoutEntryEntity>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("""
        SELECT e1.*,
        (   
            SELECT MAX(best_sets.weightInPounds / (1.0278 - 0.0278 * best_sets.reps))
            FROM SetEntity as best_sets
            INNER JOIN WorkoutEntryEntity as entries_for_reps
            ON best_sets.entryId = entries_for_reps.weId
            WHERE entries_for_reps.workoutId <> :workoutId
                AND entries_for_reps.entry_exercise_eId = e1.entry_exercise_eId
                AND best_sets.completedAt IS NOT NULL
            GROUP BY entries_for_reps.weId
        ) as rep_max
        FROM WorkoutEntryEntity as e1
        INNER JOIN SetEntity as s1 ON e1.weId = s1.entryId
        WHERE e1.workoutId = :workoutId
        GROUP BY e1.entry_exercise_eId
        HAVING rep_max IS NOT NULL
        ORDER BY rep_max
    """)
    fun getBestEntriesForWorkout(workoutId: Long): Flow<List<EntryWithSets>>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
            SELECT e1.*
            FROM WorkoutEntryEntity as e1
            INNER JOIN SetEntity as s1 ON e1.weId = s1.entryId
            INNER JOIN WorkoutEntryEntity as e2 ON e1.entry_exercise_eId = e2.entry_exercise_eId
            INNER JOIN SetEntity as s2 ON s2.entryId = e2.weId
            WHERE date(s1.completedAt) >= date(:date) 
                AND date(s2.completedAt) >= date(:date)
            GROUP BY s1.sId
            ORDER BY MAX(s1.weightInPounds / (1.0278 - 0.0278 * s1.reps)) - 
                    MIN(s2.weightInPounds / (1.0278 - 0.0278 * s2.reps))
            DESC
        """
    )
    suspend fun getMostImprovedSince(date: String): List<WorkoutEntryEntity>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
            SELECT entries.*
            FROM WorkoutEntryEntity as entries
            INNER JOIN SetEntity as sets ON entries.weId = sets.entryId
            WHERE entries.entry_exercise_eId = :exerciseId
            ORDER BY sets.weightInPounds / (1.0278 - 0.0278 * sets.reps)
            DESC
        """
    )
    suspend fun getForExercise(exerciseId: Long): List<EntryWithSets>
}
