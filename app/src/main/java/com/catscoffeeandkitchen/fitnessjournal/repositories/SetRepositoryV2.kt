package com.catscoffeeandkitchen.fitnessjournal.repositories
//
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseSetDao
//import com.catscoffeeandkitchen.data.workouts.models.SetEntity
//import com.catscoffeeandkitchen.domain.models.Exercise
//import com.catscoffeeandkitchen.domain.models.ExerciseSet
//import javax.inject.Inject
//
//class SetRepositoryV2 @Inject constructor(
//    private val setDao: ExerciseSetDao
//) {
//    suspend fun updateSet(entryId: Long, set: ExerciseSet) {
//        setDao.update(SetEntity.fromSet(entryId, set))
//    }
//
//    suspend fun updateSets(entryId: Long, sets: List<ExerciseSet>) {
//        setDao.updateAll(sets.map { SetEntity.fromSet(entryId, it) })
//    }
//
//    suspend fun addSet(entryId: Long, exercise: Exercise?) {
//        val lastSet = exercise?.id?.let { setDao.getLastSet(it) }
//        if (lastSet == null) {
//            setDao.insert(
//                SetEntity(
//                    sId = 0L,
//                    entryId = entryId,
//                    reps = 10
//                )
//            )
//        } else {
//            setDao.insert(
//                lastSet.copy(sId = 0L, entryId = entryId)
//            )
//        }
//    }
//
//    suspend fun addSet(entryId: Long, set: ExerciseSet) {
//        setDao.insert(SetEntity.fromSet(entryId, set))
//    }
//
//    suspend fun addSets(entryId: Long, sets: List<ExerciseSet>) {
//        setDao.insertAll(sets.map { SetEntity.fromSet(entryId, it) })
//    }
//
//    suspend fun deleteSets(sets: List<ExerciseSet>) {
//        setDao.deleteAll(sets.map { it.id })
//    }
//
//    suspend fun deleteSet(id: Long) {
//        setDao.delete(id)
//    }
//}