package com.catscoffeeandkitchen.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import com.catscoffeeandkitchen.room.entities.WorkoutEntity
import com.catscoffeeandkitchen.room.models.EntryWithSets
import com.catscoffeeandkitchen.room.models.WorkoutWithEntries
import com.catscoffeeandkitchen.room.models.WorkoutWithSetsAndExercises
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface WorkoutDao {
    @Transaction
    @Query("""
        SELECT *
        FROM WorkoutEntity
        WHERE WorkoutEntity.wId = :id
    """)
    fun get(id: Long): Flow<WorkoutWithEntries>

    @Transaction
    @Query("""
        SELECT * 
        FROM WorkoutEntity
    """)
    fun getAll(): Flow<List<WorkoutWithEntries>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM WorkoutEntity
        ORDER BY addedAt DESC, completedAt DESC
    """
    )
    fun getAllPaged(): PagingSource<Int, WorkoutWithSetsAndExercises>

    @Transaction
    @Query(
        """
        SELECT completedAt
        FROM WorkoutEntity
        WHERE completedAt IS NOT NULL
            AND date(completedAt) >= date(:since)
        ORDER BY completedAt DESC
    """
    )
    fun getCompletedAtSince(since: String): Flow<List<OffsetDateTime>>

    @Query("""
        SELECT MIN(completedAt)
        FROM WorkoutEntity
        WHERE completedAt IS NOT NULL AND date(completedAt) >= date(:since)
    """)
    fun getEarliestWorkoutSince(since: String = "1970-01-01"): Flow<OffsetDateTime?>

    @Query("""
        SELECT AVG(weekly_count_subquery.weekly_count)
        FROM (
            SELECT strftime('%W', completedAt) AS week_number, COUNT(*) AS weekly_count
            FROM WorkoutEntity
            WHERE completedAt IS NOT NULL AND date(completedAt) >= date(:since)
            GROUP BY week_number
        ) as weekly_count_subquery
    """)
    fun getAverageWorkoutsPerWeek(since: String = "1970-01-01"): Flow<Float?>

    @Query("""
        SELECT day_of_week FROM(
            SELECT strftime('%w', completedAt) AS day_of_week, COUNT(*) AS weekday_occurrences
            FROM WorkoutEntity
            WHERE completedAt IS NOT NULL AND date(completedAt) >= date(:since)
            GROUP BY day_of_week
            ORDER BY weekday_occurrences DESC
            LIMIT :take
        ) AS weekday_subquery
    """)
    fun getMostCommonWorkoutDays(take: Int = 3, since: String = "1970-01-01"): Flow<List<Int>?>

    @Query("""
        SELECT hour
        FROM (
            SELECT strftime('%H', completedAt) AS hour, COUNT(*) AS hour_occurrences
            FROM WorkoutEntity
            WHERE completedAt IS NOT NULL AND date(completedAt) >= date(:since)
            GROUP BY hour
            ORDER BY hour_occurrences DESC
            LIMIT :take
        )
    """)
    fun getMostCommonWorkoutTimes(take: Int = 3, since: String = "1970-01-01"): Flow<List<Int>?>

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
    fun getBestEntriesForExercisesInWorkout(workoutId: Long): Flow<List<EntryWithSets>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutEntity): Long

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Query("""
        DELETE FROM WorkoutEntity
        WHERE wId = :id
    """)
    suspend fun delete(id: Long)
}