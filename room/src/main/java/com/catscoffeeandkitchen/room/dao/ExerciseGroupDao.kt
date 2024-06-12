package com.catscoffeeandkitchen.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.catscoffeeandkitchen.room.entities.ExerciseGroupEntity
import com.catscoffeeandkitchen.room.entities.GroupExerciseXRef
import com.catscoffeeandkitchen.room.models.GroupWithExercises
import kotlinx.coroutines.flow.Flow


@Dao
interface ExerciseGroupDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(group: ExerciseGroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(groups: List<ExerciseGroupEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRef(ref: GroupExerciseXRef): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllRefs(groups: List<GroupExerciseXRef>): List<Long>

    @Update
    suspend fun updateAll(groups: List<ExerciseGroupEntity>)

    @Update
    suspend fun update(group: ExerciseGroupEntity)

    @Query(
        """
        DELETE
        FROM ExerciseGroupEntity
        WHERE gId = :groupId
    """
    )
    suspend fun delete(groupId: Long)

    @Transaction
    @Query(
        """
        SELECT *
        FROM ExerciseGroupEntity
        WHERE gId = :id
    """
    )
    fun getGroupById(id: Long): Flow<GroupWithExercises?>

    @Transaction
    @Query(
        """
        SELECT *
        FROM ExerciseGroupEntity
        WHERE gId = :id
    """
    )
    suspend fun getGroupByIdWithExercises(id: Long): GroupWithExercises

    @Transaction
    @Query(
        """
        SELECT *
        FROM ExerciseGroupEntity
    """
    )
    fun getGroups(): Flow<List<GroupWithExercises>>

    @Query(
        """
        DELETE
        FROM GroupExerciseXRef
        WHERE GroupExerciseXRef.groupId = :groupId
    """
    )
    suspend fun removeAllGroupXRefs(groupId: Long)

    @Query(
        """
        DELETE
        FROM GroupExerciseXRef
        WHERE groupId = :groupId AND exerciseId IN (:exerciseIds)
    """
    )
    suspend fun removeGroupXRefs(groupId: Long, exerciseIds: List<Long>)

    @Transaction
    @Query("""
        SELECT * 
        FROM ExerciseGroupEntity
        JOIN WorkoutEntryEntity ON WorkoutEntryEntity.entry_group_gId = gId
        WHERE WorkoutEntryEntity.workoutId = :id
    """)
    fun getInWorkout(id: Long): Flow<List<GroupWithExercises>>
}
