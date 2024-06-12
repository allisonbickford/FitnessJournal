package com.catscoffeeandkitchen.data

import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.data.ExerciseConverters.toGroup
import com.catscoffeeandkitchen.data.WorkoutPlanConverters.toEntity
import com.catscoffeeandkitchen.data.WorkoutPlanConverters.toGoal
import com.catscoffeeandkitchen.data.WorkoutPlanConverters.toPlan
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.SetAmount
import com.catscoffeeandkitchen.models.SetModifier
import com.catscoffeeandkitchen.models.SetType
import com.catscoffeeandkitchen.models.Workout
import com.catscoffeeandkitchen.models.WorkoutPlan
import com.catscoffeeandkitchen.room.entities.GoalEntity
import com.catscoffeeandkitchen.room.entities.SetEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntryEntity
import com.catscoffeeandkitchen.room.entities.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.DayOfWeek
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.math.roundToInt

class WorkoutPlanRepository @Inject constructor(
    private val workoutDataSource: WorkoutRoomDataSource,
    private val roomDataSource: WorkoutPlanRoomDataSource
) {
    fun getAllPlans(): Flow<List<WorkoutPlan>> = roomDataSource.getAllPlans().map { items ->
        items.map { result ->
            WorkoutPlan(
                id = result.plan.wpId,
                addedAt = result.plan.addedAt,
                name = result.plan.name,
                note = result.plan.note,
                goals = result.goals.map { goalData ->
                    goalData.goal.toGoal(
                        usingGroup = goalData.group?.group?.let { group ->
                            group.toGroup(
                                goalData.group?.exercises.orEmpty().map { it.toExercise() }
                            )
                        },
                        usingExercise = goalData.exercise?.toExercise()
                    )
                },
                daysOfWeek = result.plan.daysOfWeek.map { DayOfWeek.valueOf(it) }
            )
        }
    }

    fun getPlan(id: Long) = roomDataSource.getPlan(id).map { result ->
        WorkoutPlan(
            id = result.plan.wpId,
            addedAt = result.plan.addedAt,
            name = result.plan.name,
            note = result.plan.note,
            goals = result.goals.map { goal ->
                goal.goal.toGoal(
                    usingGroup = goal.group?.let { group ->
                        group.group.toGroup(
                            group.exercises.map { it.toExercise() }
                        )
                    },
                    usingExercise = goal.exercise?.toExercise()
                )
            },
            daysOfWeek = result.plan.daysOfWeek.map { DayOfWeek.valueOf(it) }
        )
    }

    fun getPlanForWorkout(workoutId: Long): Flow<WorkoutPlan?> = roomDataSource
        .getPlanForWorkout(workoutId)
        .map { planWithGoals ->
            planWithGoals?.plan?.toPlan(
                usingGoals = planWithGoals.goals.map { goalWithData ->
                    goalWithData.goal.toGoal()
                }
            )
        }

    fun nextPlanThisWeek(): Flow<WorkoutPlan?> = roomDataSource.plansThisWeek()
        .map { plans ->
            plans
                .filter { it.plan.daysOfWeek.isNotEmpty() }
                .minByOrNull { entity ->
                    entity.plan.daysOfWeek
                        .first { name ->
                            DayOfWeek.valueOf(name).value >= OffsetDateTime.now().dayOfWeek.value
                        }
                }
        }
        .map { plan ->
            plan?.plan?.toPlan(
                plan.goals.map { goal ->
                    goal.goal.toGoal(
                        usingGroup = goal.group?.let { group ->
                            group.group.toGroup(
                                group.exercises.map { it.toExercise() }
                            )
                        },
                        usingExercise = goal.exercise?.toExercise()
                    )
                }
            )
        }

    suspend fun createPlan(workoutPlan: WorkoutPlan): Long {
        return roomDataSource.createPlan(workoutPlan.toEntity())
    }

    suspend fun createPlanFromWorkout(workout: Workout) {
        val planToAdd = WorkoutPlanEntity(
            wpId = 0L,
            addedAt = OffsetDateTime.now(),
            name = "Plan from ${workout.name}",
        )
        val planId = roomDataSource.createPlan(planToAdd)

        workout.entries.forEach { entry ->
            val goal = GoalEntity(
                gId = 0L,
                exerciseId = entry.exercise?.id,
                planId = planId,
                position = entry.position,
                note = "",
                reps = entry.sets.map { it.reps }.average().roundToInt(),
                sets = entry.sets.size,
                setAmount = SetAmount.Fixed.name,
                minReps = entry.sets.minOfOrNull { it.reps },
                maxReps = entry.sets.maxOfOrNull { it.reps },
                weightInPounds = entry.sets.maxOfOrNull { it.weightInPounds },
                weightInKilograms = entry.sets.maxOfOrNull { it.weightInKilograms },
                modifiers = entry.sets.flatMap { it.modifiers.orEmpty() }.map { it.name }.distinct(),
                equipment = entry.exercise?.equipment?.map { it.name },
                repsInReserve = entry.sets.map { it.repsInReserve }.average().roundToInt(),
                perceivedExertion = entry.sets.map { it.perceivedExertion }.average().roundToInt()
            )

            roomDataSource.addGoal(goal)
        }
    }

    suspend fun createWorkoutFromPlan(planId: Long): Long {
        val planWithGoals = roomDataSource.getPlan(planId).first()
        val workout = WorkoutEntity(
            wId = 0L,
            planId = planWithGoals.plan.wpId,
            name = planWithGoals.plan.name,
            note = planWithGoals.plan.note
        )
        val workoutId = workoutDataSource.create(workout)

        Timber.d("Inserted workout $workoutId")

        planWithGoals.goals.forEachIndexed { index, goal ->
            val entryId = workoutDataSource.createEntry(
                WorkoutEntryEntity(
                    weId = 0L,
                    position = index,
                    workoutId = workoutId,
                    exercise = goal.exercise,
                    group = goal.group?.group
                )
            )

            val exercise = goal.exercise
            if (exercise != null) {
                val sets = (1..(goal.goal.sets ?: 1)).toList().map { set ->
                    SetEntity(
                        sId = 0L,
                        entryId = entryId,
                        reps = goal.goal.reps ?: 10,
                        setNumber = set,
                        weightInPounds = goal.goal.weightInPounds ?: 0f,
                        weightInKilograms = goal.goal.weightInKilograms ?: 0f,
                        repsInReserve = goal.goal.repsInReserve ?: 0,
                        perceivedExertion = goal.goal.perceivedExertion ?: 0,
                        completedAt = null,
                        seconds = goal.goal.seconds ?: 30,
                        modifier = goal.goal.modifiers,
                        type = SetType.Working.name
                    )
                }

                workoutDataSource.createSets(sets)
            }
        }

        return workoutId
    }

    suspend fun updatePlan(workoutPlan: WorkoutPlan) {
        roomDataSource.updatePlan(workoutPlan.toEntity())
    }

    suspend fun updateGoal(planId: Long, goal: Goal) {
        roomDataSource.updateGoal(goal.toEntity(planId))
    }

    suspend fun removeGoal(id: Long) {
        roomDataSource.removeGoal(id)
    }

    suspend fun addGoal(id: Long, goal: Goal) {
        roomDataSource.addGoal(goal.toEntity(id))
    }

    suspend fun moveGoal(id: Long, goal: Goal, newPosition: Int) {
        roomDataSource.getGoalInPlanWithPosition(id, goal.position)?.let {
            roomDataSource.updateGoal(it.copy(position = goal.position))
        }
        roomDataSource.updateGoal(goal.toEntity(id).copy(position = newPosition))
    }
}