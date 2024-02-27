package com.catscoffeeandkitchen.data

import com.catscoffeeandkitchen.data.ExerciseConverters.toEntity
import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.data.ExerciseConverters.toGroup
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import com.catscoffeeandkitchen.room.entities.ExerciseGroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val exerciseRoomDataSource: ExerciseRoomDataSource,
    private val roomDataSource: GroupRoomDataSource
) {
    fun get(id: Long): Flow<ExerciseGroup?> = roomDataSource.getFlow(id).map { group ->
        group?.group?.toGroup(group.exercises.map { it.toExercise() })
    }

    fun getAllGroups(): Flow<List<ExerciseGroup>> = roomDataSource.getAllGroups()
        .map { list -> list.map {
            it.group.toGroup(it.exercises.map { ex -> ex.toExercise() })
        } }

    suspend fun createGroup(groupName: String, names: List<String>): ExerciseGroup {
        val id = roomDataSource.createGroup(
            groupName,
            names
        )
        val exercises = exerciseRoomDataSource.getByNames(names)

        return ExerciseGroup(
            id = id,
            name = groupName,
            exercises = exercises.map { it.toExercise() }
        )
    }

    suspend fun removeGroup(groupId: Long) {
        roomDataSource.removeGroup(groupId)
    }

    suspend fun updateGroup(group: ExerciseGroup) {
        roomDataSource.updateGroup(group.toEntity())
    }

    suspend fun updateExercisesInGroup(group: ExerciseGroup, names: List<String>) {
        roomDataSource.setExercisesInGroup(
            group.id,
            names
        )
    }

    suspend fun addExercisesToGroup(group: ExerciseGroup, names: List<String>) {
        roomDataSource.addExercisesToGroup(group.id, names)
    }

    suspend fun removeExercisesFromGroup(group: ExerciseGroup, names: List<String>) {
        roomDataSource.removeExercisesFromGroup(group.id, names)
    }
}
