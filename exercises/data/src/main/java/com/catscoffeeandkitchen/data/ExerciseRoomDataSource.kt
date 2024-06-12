package com.catscoffeeandkitchen.data

import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.data.ExerciseConverters.toSet
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.room.dao.EntryDao
import com.catscoffeeandkitchen.room.dao.ExerciseDao
import com.catscoffeeandkitchen.room.dao.ExerciseGroupDao
import com.catscoffeeandkitchen.room.dao.ExerciseSetDao
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import com.catscoffeeandkitchen.room.entities.ExerciseGroupEntity
import com.catscoffeeandkitchen.room.entities.GroupExerciseXRef
import com.catscoffeeandkitchen.room.models.ExerciseWithAmountPerformed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExerciseRoomDataSource @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val entryDao: EntryDao,
    private val setDao: ExerciseSetDao
) {
    fun getFlow(id: Long): Flow<ExerciseEntity> = exerciseDao.get(id)

    suspend fun getByName(name: String): ExerciseEntity? {
        return exerciseDao.getExerciseByName(name)
    }

    suspend fun getByNames(names: List<String>): List<ExerciseEntity> {
        return exerciseDao.getExercisesByName(names)
    }

    suspend fun getAll(): List<ExerciseWithAmountPerformed> = exerciseDao.getAllExercises()

    suspend fun getSetsForExercise(exercise: Exercise): List<ExerciseSet> {
        return setDao.getAllCompletedSetsForExercise(exercise.id).map { it.toSet() }
    }

    fun searchExercises(
        search: String,
        muscle: String,
        category: String,
        limit: Int = 100,
        offset: Int = 0
    ) = exerciseDao.searchExercises(
        search = search,
        muscle = muscle,
        category = category,
        limit = limit,
        offset = offset
    )

    suspend fun create(entity: ExerciseEntity): Long {
        return exerciseDao.insert(entity)
    }

    suspend fun update(entity: ExerciseEntity) {
        exerciseDao.update(entity)
    }

    suspend fun mostImprovedExercise(weeksAgo: Int): ExerciseProgressStats? {
        val dateWeeksAgo = OffsetDateTime.now().minusWeeks(weeksAgo.toLong())
        var entries = entryDao.getMostImprovedSince(
            dateWeeksAgo.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
        entries = entries.filter { it.exercise?.eId == entries.firstOrNull()?.exercise?.eId }

        val sets = setDao.getSetsByEntryIds(entries.map { it.weId })
        if (entries.size < 3) {
            return null
        }

        Timber.d("${entries.map { it.exercise?.name }}")
        Timber.d("${sets.map { set -> set.completedAt } }")

        return entries.firstOrNull()?.exercise?.let { entity ->
            ExerciseProgressStats(
                exercise = entity.toExercise(),
                sets = sets
                    .sortedBy { it.weightInPounds / (1.0278 - 0.0278 * it.reps) }
                    .map { it.toSet() }
            )
        }
    }

}