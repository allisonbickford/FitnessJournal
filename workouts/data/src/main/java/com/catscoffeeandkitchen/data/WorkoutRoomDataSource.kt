package com.catscoffeeandkitchen.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.data.ExerciseConverters.toSet
import com.catscoffeeandkitchen.data.WorkoutConverters.toEntry
import com.catscoffeeandkitchen.data.WorkoutConverters.toWorkout
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.Workout
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.room.dao.EntryDao
import com.catscoffeeandkitchen.room.dao.ExerciseSetDao
import com.catscoffeeandkitchen.room.dao.GoalDao
import com.catscoffeeandkitchen.room.dao.WorkoutDao
import com.catscoffeeandkitchen.room.dao.WorkoutPlanDao
import com.catscoffeeandkitchen.room.entities.GoalEntity
import com.catscoffeeandkitchen.room.entities.SetEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntryEntity
import com.catscoffeeandkitchen.room.entities.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WorkoutRoomDataSource @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val entryDao: EntryDao,
    private val setDao: ExerciseSetDao
) {

    fun getFlow(id: Long) = workoutDao.get(id)

    fun getPagedWorkouts(): Flow<PagingData<Workout>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                workoutDao.getAllPaged()
            }
        ).flow.map { pagingData ->
            pagingData.map { data ->
                data.workout.toWorkout(
                    usingEntries = data.sets.map { set ->
                        set.entry.toEntry(
                            usingSets = set.sets.orEmpty().map { it.toSet() }
                        )
                    }
                )
            }
        }
    }

    fun getWorkoutCompletionDates(since: OffsetDateTime?): Flow<List<OffsetDateTime>> {
        return workoutDao.getCompletedAtSince(
            since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
        )
    }

    suspend fun getEntriesWithExercise(exerciseId: Long): List<WorkoutEntry> {
        return entryDao.getForExercise(exerciseId).map { entryAndSets ->
            entryAndSets.entry.toEntry(
                usingSets = entryAndSets.sets.orEmpty().map { it.toSet() }
            )
        }
    }

    suspend fun create(entity: WorkoutEntity): Long {
        return workoutDao.insert(entity)
    }

    suspend fun removeWorkout(workoutId: Long) {
        workoutDao.delete(workoutId)
    }

    suspend fun update(entity: WorkoutEntity) {
        return workoutDao.update(entity)
    }

    fun getBestEntriesForExercisesInWorkout(workoutId: Long)
        = workoutDao.getBestEntriesForExercisesInWorkout(workoutId)

    fun getEarliestWorkoutCompletedSince(since: OffsetDateTime? = null): Flow<OffsetDateTime?>
        = workoutDao.getEarliestWorkoutSince(
            since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
        )

    fun getAverageWorkoutsPerWeekSince(since: OffsetDateTime? = null): Flow<Float?>
        = workoutDao.getAverageWorkoutsPerWeek(
        since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
        )

    fun getMostCommonWorkoutDaysSince(since: OffsetDateTime? = null): Flow<List<DayOfWeek>>
        = workoutDao.getMostCommonWorkoutDays(
            since = since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
        ).map { days -> days.orEmpty().map { day ->
                if (day == 0) DayOfWeek.SUNDAY
                else DayOfWeek.entries.first { it.value == day }
            }
        }

    fun getMostCommonWorkoutTimesSince(since: OffsetDateTime? = null): Flow<List<Int>?>
        = workoutDao.getMostCommonWorkoutTimes(
            since = since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
        )

    fun getMostRecentEntries(take: Int): Flow<List<WorkoutEntryEntity>> = entryDao.getLatest(take)


    suspend fun createEntry(entity: WorkoutEntryEntity): Long {
        return entryDao.insert(entity)
    }

    suspend fun createAllEntries(entities: List<WorkoutEntryEntity>): List<Long> {
        return entryDao.insertAll(entities)
    }

    suspend fun removeEntry(id: Long) {
        entryDao.delete(id)
    }

    suspend fun updateEntry(entry: WorkoutEntryEntity) {
        entryDao.update(entry)
    }

    suspend fun getEntryInWorkoutAtPosition(workoutId: Long, position: Int): WorkoutEntryEntity? {
        return entryDao.getInWorkoutAtPosition(workoutId, position)
    }

    suspend fun createSet(set: SetEntity): Long {
        return setDao.insert(set)
    }

    suspend fun createSets(sets: List<SetEntity>): List<Long> {
        return setDao.insertAll(sets)
    }

    suspend fun updateSet(sets: SetEntity) {
        setDao.update(sets)
    }

    suspend fun updateSets(sets: List<SetEntity>) {
        setDao.updateAll(sets)
    }

    suspend fun removeAllSets(sets: List<Long>) {
        setDao.deleteAll(sets)
    }

    suspend fun removeSet(id: Long) {
        setDao.delete(id)
    }

    suspend fun getLastSetOfExercise(exerciseId: Long?): SetEntity? {
        return exerciseId?.let { setDao.getLastSet(it) }
    }
}