package com.catscoffeeandkitchen.fitnessjournal.repositories
//
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import androidx.paging.map
//import com.catscoffeeandkitchen.data.workouts.db.EntryDao
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseGroupDao
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseSetDao
//import com.catscoffeeandkitchen.data.workouts.db.GoalDao
//import com.catscoffeeandkitchen.data.workouts.db.WorkoutDaoV2
//import com.catscoffeeandkitchen.data.workouts.db.WorkoutPlanDaoV2
//import com.catscoffeeandkitchen.data.workouts.models.SetEntity
//import com.catscoffeeandkitchen.data.workouts.models.WorkoutEntity
//import com.catscoffeeandkitchen.data.workouts.models.v2.WorkoutEntryEntity
//import com.catscoffeeandkitchen.domain.models.ExerciseSet
//import com.catscoffeeandkitchen.domain.models.ExerciseSetType
//import com.catscoffeeandkitchen.domain.models.Workout
//import com.catscoffeeandkitchen.domain.models.WorkoutEntry
//import com.catscoffeeandkitchen.domain.models.WeightUnit
//import com.catscoffeeandkitchen.domain.models.WorkoutWeekStats
//import com.patrykandpatrick.vico.core.extension.orZero
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.map
//import timber.log.Timber
//import java.time.DayOfWeek
//import java.time.OffsetDateTime
//import java.time.format.DateTimeFormatter
//import javax.inject.Inject
//import kotlin.math.roundToInt
//
//class WorkoutRepositoryV2 @Inject constructor(
//    private val setDao: ExerciseSetDao,
//    private val entryDao: EntryDao,
//    private val goalDao: GoalDao,
//    private val groupDao: ExerciseGroupDao,
//    private val workoutPlanDaoV2: WorkoutPlanDaoV2,
//    private val workoutDaoV2: WorkoutDaoV2
//) {
//    fun getAllWorkouts(): Flow<List<Workout>> = workoutDaoV2.getAll().map { items ->
//        items.map { result ->
//            Workout(
//                id = result.workout.wId,
//                addedAt = result.workout.addedAt,
//                name = result.workout.name,
//                note = result.workout.note,
//                entries = result.entries.map { data ->
//                    data.entry.toEntry(
//                        sets = data.sets.orEmpty().map { set ->
//                            set.toExerciseSet()
//                        }
//                    )
//                }
//            )
//        }
//    }
//
//    fun getPagedWorkouts(): Flow<PagingData<Workout>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 20,
//                enablePlaceholders = false,
//                prefetchDistance = 5
//            ),
//            pagingSourceFactory = {
//                workoutDaoV2.getAllPaged()
//            }
//        ).flow.map { pagingData ->
//            pagingData.map { data ->
//                data.workout.toWorkout().copy(
//                    entries = emptyList(),
//                ) }
//        }
//    }
//
//    fun getWorkout(id: Long) = workoutDaoV2.get(id)
//        .combine(groupDao.getInWorkout(id)) { workout, groups ->
//            Timber.d("${workout.entries}")
//            Timber.d("$groups")
//            Workout(
//                id = workout.workout.wId,
//                addedAt = workout.workout.addedAt,
//                name = workout.workout.name,
//                note = workout.workout.note,
//                entries = workout.entries.map { entryData ->
//                    entryData.entry.toEntry(
//                        groupWithExercises = groups
//                            .firstOrNull { it.group.gId == entryData.entry.group?.gId },
//                        sets = entryData.sets.orEmpty().map { set ->
//                            set.toExerciseSet()
//                        }.sortedBy { it.setNumber }
//                    )
//                }.sortedBy { it.position }
//            )
//        }
//
//    fun getBestEntries(workoutId: Long): Flow<List<WorkoutEntry>> = entryDao
//        .getBestEntriesForWorkout(workoutId).map { list -> list.map { it.toEntry() } }
//
//    fun getWeekStats(since: OffsetDateTime? = null): Flow<WorkoutWeekStats> = combine(
//                workoutDaoV2.getEarliestWorkoutSince(
//                    since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
//                ),
//                workoutDaoV2.getAverageWorkoutsPerWeek(
//                    since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
//                ),
//                workoutDaoV2.getMostCommonWorkoutDays(
//                    since = since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
//                ),
//                workoutDaoV2.getMostCommonWorkoutTimes(
//                    since = since?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: "1970-01-01"
//                )
//            ) { earliest, avg, days, times ->
//                WorkoutWeekStats(
//                    since = earliest,
//                    averageWorkoutsPerWeek = avg.orZero,
//                    mostCommonDays = days.orEmpty()
//                        .map { day ->
//                            if (day == 0) DayOfWeek.SUNDAY
//                            else DayOfWeek.entries.first { it.value == day }
//                        },
//                    mostCommonTimes = times.orEmpty()
//                )
//            }
//
//    fun mostRecentEntries(take: Int): Flow<List<WorkoutEntry>> = entryDao.getLatest(take)
//        .map { latest ->
//            latest.map { it.toEntry() }
//        }
//
//    suspend fun createWorkout(): Long {
//        return workoutDaoV2.insert(WorkoutEntity(wId = 0L))
//    }
//
//    suspend fun removeWorkout(id: Long) {
//        workoutDaoV2.delete(id)
//    }
//
//    suspend fun updateWorkout(workout: Workout) {
//        workoutDaoV2.update(WorkoutEntity.fromWorkout(workout))
//    }
//
//    suspend fun addEntry(id: Long, entry: WorkoutEntry): Long {
//        return entryDao.insert(WorkoutEntryEntity.fromEntry(id, entry))
//    }
//
//    suspend fun addEntries(id: Long, entries: List<WorkoutEntry>) {
//        entryDao.insertAll(entries.map { WorkoutEntryEntity.fromEntry(id, it) })
//    }
//
//    suspend fun removeEntry(entry: WorkoutEntry) {
//        entryDao.delete(entry.id)
//    }
//
//    suspend fun updateEntry(workoutId: Long, entry: WorkoutEntry) {
//        Timber.d("Updating Entry ${entry.position} to $entry")
//        entryDao.update(WorkoutEntryEntity.fromEntry(workoutId, entry))
//    }
//
//    suspend fun moveEntry(workoutId: Long, entry: WorkoutEntry, position: Int) {
//        entryDao.getInWorkoutAtPosition(workoutId, position)?.let { atDesiredPosition ->
//            entryDao.update(atDesiredPosition.copy(position = entry.position))
//        }
//        entryDao.update(WorkoutEntryEntity.fromEntry(workoutId, entry).copy(position = position))
//    }
//
//    suspend fun addWarmupSets(entry: WorkoutEntry, unit: WeightUnit) {
//        val highWeight = entry.sets.orEmpty()
//            .maxOf {
//                if (unit == WeightUnit.Pounds) {
//                    it.weightInPounds
//                } else {
//                    it.weightInKilograms
//                }
//            }
//
//        val increments = mapOf(
//            .25 to 12,
//            .575 to 8,
//            .725 to 5,
//            .825 to 3,
//            .925 to 1
//        )
//
//        val setsToAdd = increments.entries.mapIndexed { index, warmupSet ->
//            ExerciseSet(
//                0L,
//                reps = warmupSet.value,
//                setNumber = index + 1,
//                weightInPounds = roundToNearest(warmupSet.key * highWeight, 5).toFloat(),
//                weightInKilograms = roundToNearest(warmupSet.key * highWeight, 5).toFloat(),
//                type = ExerciseSetType.WarmUp
//            )
//        }
//
//        setDao.insertAll(
//            setsToAdd.map { SetEntity.fromSet(entry.id, it) }
//        )
//        setDao.updateAll(entry.sets.orEmpty().map { set ->
//            SetEntity.fromSet(entry.id, set.copy(setNumber = set.setNumber + setsToAdd.size))
//        })
//    }
//
//    private fun roundToNearest(value: Double, nearest: Int): Int {
//        return (value / nearest).roundToInt() * nearest
//    }
//}