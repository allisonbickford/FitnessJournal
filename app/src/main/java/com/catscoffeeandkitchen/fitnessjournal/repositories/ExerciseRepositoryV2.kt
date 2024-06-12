package com.catscoffeeandkitchen.fitnessjournal.repositories
//
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import com.catscoffeeandkitchen.data.workouts.db.EntryDao
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseDao
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseGroupDao
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseSetDao
//import com.catscoffeeandkitchen.data.workouts.models.ExerciseGroupEntity
//import com.catscoffeeandkitchen.data.workouts.models.GroupExerciseXRef
//import com.catscoffeeandkitchen.data.workouts.models.exercise.ExerciseEntity
//import com.catscoffeeandkitchen.data.workouts.network.ExerciseSearchService
//import com.catscoffeeandkitchen.data.workouts.network.WgerPagingSource
//import com.catscoffeeandkitchen.domain.models.Exercise
//import com.catscoffeeandkitchen.domain.models.ExerciseGroup
//import com.catscoffeeandkitchen.domain.models.ExerciseProgressStats
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//import timber.log.Timber
//import java.time.OffsetDateTime
//import java.time.format.DateTimeFormatter
//import javax.inject.Inject
//
//class ExerciseRepositoryV2 @Inject constructor(
//    private val exerciseDao: ExerciseDao,
//    private val groupDao: ExerciseGroupDao,
//    private val entryDao: EntryDao,
//    private val setDao: ExerciseSetDao,
//    private val exerciseSearchService: ExerciseSearchService
//) {
//    fun get(id: Long): Flow<Exercise> = exerciseDao
//        .get(id).map { it.toExercise() }
//
//    suspend fun getOrCreate(name: String): Exercise {
//        val exercise = exerciseDao.getExerciseByName(name)
//
//        if (exercise == null) {
//            val id = exerciseDao.insert(ExerciseEntity(0L, name = name))
//            return Exercise(id = id, name = name)
//        }
//
//        return exercise.toExercise()
//    }
//
//    fun searchPagedExercise(
//        search: String,
//        muscle: String,
//        category: String
//    ): Flow<PagingData<Exercise>> = Pager(
//        config = PagingConfig(
//            pageSize = 20,
//            prefetchDistance = 5,
//            enablePlaceholders = false
//        ),
//        pagingSourceFactory = {
//            WgerPagingSource(
//                search, muscle, category, exerciseSearchService
//            )
//        }
//    ).flow
//
//    suspend fun createGroup(groupName: String, names: List<String>): ExerciseGroup {
//        val id = groupDao.insert(ExerciseGroupEntity(gId = 0, name = groupName))
//
//        val exercises = exerciseDao.getExercisesByName(names)
//        groupDao.insertAllRefs(exercises.map { ex ->
//            GroupExerciseXRef(
//                egxrId = 0L,
//                groupId = id,
//                exerciseId = ex.eId
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
//    suspend fun updateGroup(group: ExerciseGroup, exerciseNames: List<String>) {
//        val exercises = exerciseDao.getExercisesByName(exerciseNames)
//        groupDao.removeGroupXRefs(group.id)
//        groupDao.insertAllRefs(exercises.map { ex ->
//            GroupExerciseXRef(0L, exerciseId = ex.eId, groupId = group.id)
//        })
//    }
//
//    suspend fun mostImprovedExercise(weeksAgo: Int): ExerciseProgressStats? {
//        val dateWeeksAgo = OffsetDateTime.now().minusWeeks(weeksAgo.toLong())
//        var entries = entryDao.getMostImprovedSince(
//            dateWeeksAgo.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
//        )
//        entries = entries.filter { it.exercise?.eId == entries.firstOrNull()?.exercise?.eId }
//
//        val sets = setDao.getSetsByEntryIds(entries.map { it.weId })
//        if (entries.size < 3) {
//            return null
//        }
//
//        Timber.d("${entries.map { it.exercise?.name }}")
//        Timber.d("${sets.map { set -> set.completedAt } }")
//
//        return ExerciseProgressStats(
//            exercise = entries.first().exercise!!.toExercise(),
//            sets = sets
//                .sortedBy { it.weightInPounds / (1.0278 - 0.0278 * it.reps) }
//                .map { it.toExerciseSet() }
//        )
//    }
//}
