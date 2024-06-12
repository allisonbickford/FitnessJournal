package com.catscoffeeandkitchen.data

import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.models.WorkoutPlan
import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.data.ExerciseConverters.toGroup
import com.catscoffeeandkitchen.data.ExerciseConverters.toSet
import com.catscoffeeandkitchen.models.ExerciseSet
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
import com.catscoffeeandkitchen.room.models.PlanWithGoals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.DayOfWeek
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class WorkoutPlanRoomDataSource @Inject constructor(
    private val workoutPlanDao: WorkoutPlanDao,
    private val goalDao: GoalDao,
    private val setDao: ExerciseSetDao
) {
    fun getAllPlans(): Flow<List<PlanWithGoals>> = workoutPlanDao.getAll()

    fun getPlan(id: Long) = workoutPlanDao.get(id)

    suspend fun getGoalInPlanWithPosition(planId: Long, position: Int): GoalEntity? {
        return goalDao.getInPlanAtPosition(planId, position)
    }

    fun getPlanForWorkout(workoutId: Long): Flow<PlanWithGoals?>
        = workoutPlanDao.getForWorkout(workoutId)

    fun plansThisWeek(): Flow<List<PlanWithGoals>> = combine(
        (0 until ChronoUnit.DAYS.between(
            OffsetDateTime.now(),
            OffsetDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
        )).map { days ->
            workoutPlanDao.getNextPlanWithDay(
                OffsetDateTime.now().plusDays(days).dayOfWeek.name
            )
        }
    ) { flows ->
        flows.filterNotNull()
    }

    suspend fun createPlan(plan: WorkoutPlanEntity): Long {
        return workoutPlanDao.insert(plan)
    }

    suspend fun updatePlan(plan: WorkoutPlanEntity) {
        workoutPlanDao.update(plan)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.update(goal)
    }

    suspend fun removeGoal(id: Long) {
        goalDao.delete(id)
    }

    suspend fun addGoal(goal: GoalEntity): Long {
        return goalDao.insert(goal)
    }

    suspend fun addSets(sets: List<SetEntity>) {
        setDao.insertAll(sets)
    }

}