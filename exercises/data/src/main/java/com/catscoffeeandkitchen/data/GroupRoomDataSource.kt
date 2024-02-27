package com.catscoffeeandkitchen.data

import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.data.ExerciseConverters.toGroup
import com.catscoffeeandkitchen.data.ExerciseConverters.toSet
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.room.dao.EntryDao
import com.catscoffeeandkitchen.room.dao.ExerciseDao
import com.catscoffeeandkitchen.room.dao.ExerciseGroupDao
import com.catscoffeeandkitchen.room.dao.ExerciseSetDao
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import com.catscoffeeandkitchen.room.entities.ExerciseGroupEntity
import com.catscoffeeandkitchen.room.entities.GroupExerciseXRef
import com.catscoffeeandkitchen.room.models.GroupWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GroupRoomDataSource @Inject constructor(
    private val groupDao: ExerciseGroupDao,
    private val exerciseDao: ExerciseDao,
    private val entryDao: EntryDao,
    private val setDao: ExerciseSetDao
) {
    fun getFlow(id: Long): Flow<GroupWithExercises?> = groupDao.getGroupById(id)

    fun getAllGroups(): Flow<List<GroupWithExercises>> = groupDao.getGroups()

    suspend fun createGroup(groupName: String, names: List<String>): Long {
        val id = groupDao.insert(ExerciseGroupEntity(gId = 0, name = groupName))
        val exercises = exerciseDao.getExercisesByName(names)

        var ids = exercises.map { it.eId }
        if (exercises.isEmpty()) {
            ids = exerciseDao.insertAll(
                names.map {
                    ExerciseEntity(eId = 0L, name = it)
                }
            )
        }

        groupDao.insertAllRefs(ids.map { eId ->
            GroupExerciseXRef(
                egxrId = 0L,
                groupId = id,
                exerciseId = eId
            )
        })

        return id
    }

    suspend fun removeGroup(groupId: Long) {
        groupDao.delete(groupId)
    }

    suspend fun updateGroup(entity: ExerciseGroupEntity) {
        groupDao.update(entity)
    }

    suspend fun addExercisesToGroup(groupId: Long, names: List<String>) {
        val exercises = exerciseDao.getExercisesByName(names)
        groupDao.insertAllRefs(exercises
            .map { exercise ->
                GroupExerciseXRef(
                    egxrId = 0L,
                    exerciseId = exercise.eId,
                    groupId = groupId
                )
            })
    }

    suspend fun removeExercisesFromGroup(groupId: Long, names: List<String>) {
        val exercises = exerciseDao.getExercisesByName(names)
        groupDao.removeGroupXRefs(
            groupId,
            exercises.map { it.eId }
        )
    }

    suspend fun setExercisesInGroup(groupId: Long, names: List<String>) {
        groupDao.removeAllGroupXRefs(groupId)

        val exercises = exerciseDao.getExercisesByName(names)
        groupDao.insertAllRefs(exercises
            .map { exercise ->
                GroupExerciseXRef(
                    egxrId = 0L,
                    exerciseId = exercise.eId,
                    groupId = groupId
                )
            })
    }

}