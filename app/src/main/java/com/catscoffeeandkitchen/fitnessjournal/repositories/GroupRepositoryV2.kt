package com.catscoffeeandkitchen.fitnessjournal.repositories
//
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseDao
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseGroupDao
//import com.catscoffeeandkitchen.data.workouts.models.ExerciseGroupEntity
//import com.catscoffeeandkitchen.data.workouts.models.GroupExerciseXRef
//import com.catscoffeeandkitchen.data.workouts.models.exercise.ExerciseEntity
//import com.catscoffeeandkitchen.domain.models.ExerciseGroup
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//import javax.inject.Inject
//
//class GroupRepositoryV2 @Inject constructor(
//    private val exerciseDao: ExerciseDao,
//    private val groupDao: ExerciseGroupDao
//) {
//    fun get(id: Long): Flow<ExerciseGroup?> = groupDao.getGroupById(id)
//            .map { it?.toExerciseGroup() }
//
//    fun getAllGroups(): Flow<List<ExerciseGroup>> = groupDao.getGroups()
//        .map { list -> list.map { it.toExerciseGroup() } }
//
//    suspend fun createGroup(groupName: String, names: List<String>): ExerciseGroup {
//        val id = groupDao.insert(ExerciseGroupEntity(gId = 0, name = groupName))
//        val exercises = exerciseDao.getExercisesByName(names)
//
//        var ids = exercises.map { it.eId }
//        if (exercises.isEmpty()) {
//            ids = exerciseDao.insertAll(
//                names.map {
//                    ExerciseEntity(eId = 0L, name = it)
//                }
//            )
//        }
//
//        groupDao.insertAllRefs(ids.map { eId ->
//            GroupExerciseXRef(
//                egxrId = 0L,
//                groupId = id,
//                exerciseId = eId
//            )
//        })
//
//        return ExerciseGroup(
//            id = id,
//            name = groupName,
//            exercises = exercises.map { it.toExercise() }
//        )
//    }
//
//    suspend fun removeGroup(groupId: Long) {
//        groupDao.delete(groupId)
//    }
//
//    suspend fun updateGroup(group: ExerciseGroup) {
//        ExerciseGroupEntity.fromGroup(group)?.let { groupDao.update(it) }
//    }
//
//    suspend fun updateExercisesInGroup(group: ExerciseGroup, names: List<String>) {
//        groupDao.removeGroupXRefs(group.id)
//
//        val exercises = exerciseDao.getExercisesByName(names)
//        groupDao.insertAllRefs(exercises
//            .map { exercise ->
//                GroupExerciseXRef(
//                    egxrId = 0L,
//                    exerciseId = exercise.eId,
//                    groupId = group.id
//                )
//            })
//    }
//
//    suspend fun addExerciseToGroup(group: ExerciseGroup, name: String) {
//        exerciseDao.getExerciseByName(name)?.let { entity ->
//            groupDao.insertRef(GroupExerciseXRef(
//                egxrId = 0L,
//                exerciseId = entity.eId,
//                groupId = group.id
//            ))
//        }
//    }
//}