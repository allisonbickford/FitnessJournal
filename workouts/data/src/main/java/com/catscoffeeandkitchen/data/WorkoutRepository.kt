package com.catscoffeeandkitchen.data

import androidx.paging.PagingData
import com.catscoffeeandkitchen.data.ExerciseConverters.toEntity
import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.data.ExerciseConverters.toSet
import com.catscoffeeandkitchen.data.WorkoutConverters.toEntity
import com.catscoffeeandkitchen.data.WorkoutConverters.toEntry
import com.catscoffeeandkitchen.data.WorkoutConverters.toWorkout
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.Workout
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.models.WorkoutWeekStats
import com.catscoffeeandkitchen.room.entities.SetEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt

class WorkoutRepository @Inject constructor(
    private val roomDataSource: WorkoutRoomDataSource
) {
    fun getPagedWorkouts(): Flow<PagingData<Workout>> = roomDataSource.getPagedWorkouts()

    fun get(workoutId: Long): Flow<Workout> = roomDataSource.getFlow(workoutId).map { workout ->
        workout.workout.toWorkout(
            usingEntries = workout.entries.map { entry ->
                entry.entry.toEntry(
                    usingSets = entry.sets.orEmpty().map { it.toSet() }
                )
            }
        )
    }

    fun getCompletedAtDates(months: Long): Flow<List<OffsetDateTime>> = roomDataSource.getWorkoutCompletionDates(
        OffsetDateTime.now().minusMonths(months)
    )

    suspend fun removeWorkout(workoutId: Long) {
        roomDataSource.removeWorkout(workoutId)
    }

    fun getPersonalBests(workoutId: Long): Flow<List<WorkoutEntry>> = roomDataSource
        .getBestEntriesForExercisesInWorkout(workoutId)
        .map { list ->
            list.map { entryWithSets ->
                entryWithSets.entry.toEntry(
                    usingSets = entryWithSets.sets.orEmpty().map { it.toSet() }
                )
            }
        }

    fun getWeekStats(since: OffsetDateTime? = null): Flow<WorkoutWeekStats> = combine(
        roomDataSource.getEarliestWorkoutCompletedSince(since),
        roomDataSource.getAverageWorkoutsPerWeekSince(since),
        roomDataSource.getMostCommonWorkoutDaysSince(since),
        roomDataSource.getMostCommonWorkoutTimesSince(since)
    ) { earliest, avg, days, times ->
        WorkoutWeekStats(
            since = earliest,
            averageWorkoutsPerWeek = avg,
            mostCommonDays = days,
            mostCommonTimes = times.orEmpty()
        )
    }

    fun mostRecentEntries(take: Int): Flow<List<WorkoutEntry>> = roomDataSource
        .getMostRecentEntries(take)
        .map { latest ->
            latest.map { entry ->
                entry.toEntry(emptyList())
            }
        }

    suspend fun getSetsForExercise(exercise: Exercise): List<WorkoutEntry> {
        return roomDataSource.getEntriesWithExercise(exercise.id)
    }

    suspend fun createWorkout(): Long {
        return roomDataSource.create(WorkoutEntity(wId = 0L))
    }


    suspend fun updateWorkout(workout: Workout) {
        roomDataSource.update(workout.toEntity())
    }

    suspend fun addEntry(id: Long, entry: WorkoutEntry): Long {
        return roomDataSource.createEntry(entry.toEntity(id))
    }

    suspend fun addEntries(id: Long, entries: List<WorkoutEntry>) {
        roomDataSource.createAllEntries(entries.map { it.toEntity(id) })
    }

    suspend fun removeEntry(entry: WorkoutEntry) {
        roomDataSource.removeEntry(entry.id)
    }

    suspend fun updateEntry(workoutId: Long, entry: WorkoutEntry) {
        Timber.d("Updating Entry ${entry.position} to $entry")
        roomDataSource.updateEntry(entry.toEntity(workoutId))
    }

    suspend fun moveEntry(workoutId: Long, entry: WorkoutEntry, position: Int) {
        roomDataSource.getEntryInWorkoutAtPosition(workoutId, position)?.let { atDesiredPosition ->
            roomDataSource.updateEntry(atDesiredPosition.copy(position = entry.position))
        }
        roomDataSource.updateEntry(entry.toEntity(workoutId).copy(position = position))
    }

    suspend fun addWarmupSets(entry: WorkoutEntry, unit: WeightUnit) {
        val highWeight = entry.sets.orEmpty()
            .maxOf {
                if (unit == WeightUnit.Pounds) {
                    it.weightInPounds
                } else {
                    it.weightInKilograms
                }
            }

        val increments = mapOf(
            .25 to 12,
            .575 to 8,
            .725 to 5,
            .825 to 3,
            .925 to 1
        )

        val setsToAdd = increments.entries.mapIndexed { index, warmupSet ->
            ExerciseSet(
                0L,
                reps = warmupSet.value,
                setNumber = index + 1,
                weightInPounds = roundToNearest(warmupSet.key * highWeight, 5).toFloat(),
                weightInKilograms = roundToNearest(warmupSet.key * highWeight, 5).toFloat(),
                type = SetType.WarmUp
            )
        }

        roomDataSource.createSets(setsToAdd.map { it.toEntity(entryId = entry.id) })
        roomDataSource.updateSets(
            entry.sets.map { set ->
                set.toEntity(entry.id).copy(setNumber = set.setNumber + setsToAdd.size)
            }
        )
    }

    suspend fun addSet(entryId: Long, set: ExerciseSet) {
        roomDataSource.createSet(
            set.toEntity(entryId = entryId).copy(completedAt = null)
        )
    }

    suspend fun addSetWithExercise(entryId: Long, exercise: Exercise?) {
        val lastSet = roomDataSource.getLastSetOfExercise(exercise?.id)
        if (lastSet == null) {
            roomDataSource.createSet(
                SetEntity(
                    sId = 0L,
                    entryId = entryId,
                    type = SetType.Working.name
                )
            )
        } else {
            roomDataSource.createSet(
                lastSet.copy(sId = 0L, entryId = entryId)
            )
        }
    }

    suspend fun updateSet(entryId: Long, set: ExerciseSet) {
        roomDataSource.updateSet(set.toEntity(entryId = entryId))
    }

    suspend fun updateSets(entryId: Long, sets: List<ExerciseSet>) {
        roomDataSource.updateSets(sets.map { it.toEntity(entryId = entryId)})
    }

    suspend fun removeSet(id: Long) {
        roomDataSource.removeSet(id)
    }

    suspend fun removeSets(ids: List<Long>) {
        roomDataSource.removeAllSets(ids)
    }

    private fun roundToNearest(value: Double, nearest: Int): Int {
        return (value / nearest).roundToInt() * nearest
    }
}