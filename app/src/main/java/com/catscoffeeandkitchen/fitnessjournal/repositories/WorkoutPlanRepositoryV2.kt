package com.catscoffeeandkitchen.fitnessjournal.repositories
//
//import com.catscoffeeandkitchen.data.workouts.db.EntryDao
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseDao
//import com.catscoffeeandkitchen.data.workouts.db.ExerciseSetDao
//import com.catscoffeeandkitchen.data.workouts.db.GoalDao
//import com.catscoffeeandkitchen.data.workouts.db.WorkoutDaoV2
//import com.catscoffeeandkitchen.data.workouts.db.WorkoutPlanDaoV2
//import com.catscoffeeandkitchen.data.workouts.models.ExerciseGoal
//import com.catscoffeeandkitchen.data.workouts.models.SetEntity
//import com.catscoffeeandkitchen.data.workouts.models.WorkoutEntity
//import com.catscoffeeandkitchen.data.workouts.models.WorkoutPlanEntity
//import com.catscoffeeandkitchen.data.workouts.models.v2.GoalEntity
//import com.catscoffeeandkitchen.data.workouts.models.v2.WorkoutEntryEntity
//import com.catscoffeeandkitchen.domain.models.Goal
//import com.catscoffeeandkitchen.domain.models.Workout
//import com.catscoffeeandkitchen.domain.models.WorkoutPlan
//import com.catscoffeeandkitchen.domain.util.DataState
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asFlow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.filter
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.onEach
//import timber.log.Timber
//import java.time.DayOfWeek
//import java.time.OffsetDateTime
//import java.time.temporal.ChronoUnit
//import java.time.temporal.TemporalAdjusters
//import javax.inject.Inject
//import kotlin.math.roundToInt
//
//class WorkoutPlanRepositoryV2 @Inject constructor(
//    private val goalDao: GoalDao,
//    private val entryDao: EntryDao,
//    private val setDao: ExerciseSetDao,
//    private val workoutPlanDaoV2: WorkoutPlanDaoV2,
//    private val workoutDaoV2: WorkoutDaoV2
//) {
//    fun getAllPlans(): Flow<List<WorkoutPlan>> = workoutPlanDaoV2.getAll().map { items ->
//        items.map { result ->
//            WorkoutPlan(
//                id = result.plan.wpId,
//                addedAt = result.plan.addedAt,
//                name = result.plan.name,
//                note = result.plan.note,
//                goals = result.goals.map { it.goal.toGoal(
//                    it.group?.group?.toExerciseGroupFromEntity(it.group?.exercises.orEmpty())) },
//                daysOfWeek = result.plan.daysOfWeek.map { DayOfWeek.valueOf(it) }
//            )
//        }
//    }
//
//    fun getPlan(id: Long) = workoutPlanDaoV2.get(id).map { result ->
//        WorkoutPlan(
//            id = result.plan.wpId,
//            addedAt = result.plan.addedAt,
//            name = result.plan.name,
//            note = result.plan.note,
//            goals = result.goals.map { it.goal.toGoal(
//                it.group?.group?.toExerciseGroupFromEntity(it.group?.exercises.orEmpty())) },
//            daysOfWeek = result.plan.daysOfWeek.map { DayOfWeek.valueOf(it) }
//        )
//    }
//
//    fun nextPlanThisWeek(): Flow<WorkoutPlan?> = combine(
//            (0 until ChronoUnit.DAYS.between(
//                OffsetDateTime.now(),
//                OffsetDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
//            )).map { days ->
//                workoutPlanDaoV2.getNextPlanWithDay(
//                    OffsetDateTime.now().plusDays(days).dayOfWeek.name
//                )
//            }
//        ) { flows ->
//            flows.filterNotNull()
//        }.map { plans ->
//            plans
//                .sortedBy { plan ->
//                    plan.plan.daysOfWeek
//                        .firstOrNull { name ->
//                            DayOfWeek.valueOf(name).value >= OffsetDateTime.now().dayOfWeek.value
//                        }
//                }
//                .firstOrNull()?.plan?.toPlan()
//        }
//        .onEach {  }
//
//
//    suspend fun createPlanFromWorkout(workout: Workout) {
//        val planToAdd = WorkoutPlanEntity(
//            wpId = 0L,
//            addedAt = OffsetDateTime.now(),
//            name = "Plan from ${workout.name}",
//        )
//        val planId = workoutPlanDaoV2.insert(planToAdd)
//
//        val goals = workout.entries.map { entry ->
//            Goal(
//                id = 0L,
//                exercise = entry.exercise,
//                position = entry.position,
//                sets = entry.sets.size,
//                reps = entry.sets.map { it.reps }.average().roundToInt(),
//                minReps = entry.sets.minOf { it.reps },
//                maxReps = entry.sets.maxOf { it.reps },
//                rir = entry.sets.map { it.repsInReserve }.average().roundToInt(),
//                perceivedExertion = entry.sets.map { it.perceivedExertion }.average().roundToInt(),
//                note = "",
//            )
//        }
//        goalDao.insertAll(goals.map { GoalEntity.fromGoal(it, planId) })
//    }
//
//    suspend fun createWorkoutFromPlan(planId: Long): Long {
//        val plan = workoutPlanDaoV2.get(planId).first()
//
//        val workout = WorkoutEntity(
//            wId = 0L,
//            planId = planId,
//            name = "${plan.plan.name} Workout",
//            note = plan.plan.note
//        )
//        val workoutId = workoutDaoV2.insert(workout)
//
//        Timber.d("Inserted workout $workoutId")
//
//        plan.goals.forEachIndexed { index, goal ->
//            val entryId = entryDao.insert(
//                WorkoutEntryEntity(
//                    weId = 0L,
//                    position = index,
//                    workoutId = workoutId,
//                    goal = goal.goal,
//                    exercise = goal.goal.exercise,
//                    group = goal.goal.group
//                )
//            )
//
//            val exercise = goal.goal.exercise
//            if (exercise != null) {
//                val sets = (1..goal.goal.sets).map { setNumber ->
//                    SetEntity(
//                        sId = 0L,
//                        entryId = entryId,
//                        reps = goal.goal.reps,
//                        weightInPounds = goal.goal.weightInPounds,
//                        weightInKilograms = goal.goal.weightInKilograms,
//                        repsInReserve = goal.goal.repsInReserve,
//                        perceivedExertion = goal.goal.perceivedExertion,
//                        setNumber = setNumber,
//                        type = goal.goal.type,
//                        modifier = goal.goal.modifier,
//                    )
//                }
//
//                setDao.insertAll(sets)
//            }
//        }
//
//        return workoutId
//    }
//
//    suspend fun updateGoal(wpId: Long, goal: Goal) {
//        goalDao.update(GoalEntity.fromGoal(goal, wpId))
//    }
//
//    suspend fun removeGoal(id: Long) {
//        goalDao.delete(id)
//    }
//
//    suspend fun addGoal(id: Long, goal: Goal) {
//        goalDao.insert(GoalEntity.fromGoal(goal, id))
//    }
//}