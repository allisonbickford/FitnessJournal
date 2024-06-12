package com.catscoffeeandkitchen.ui

import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.Goal
import java.time.DayOfWeek

sealed class PlanAction {
    data object AddExercise: PlanAction()
    data object AddExerciseGroup: PlanAction()
    class RemoveExercise(val exercise: Exercise): PlanAction()

    class UpdateWorkoutName(val name: String): PlanAction()
    class UpdateWorkoutNotes(val notes: String): PlanAction()
    class UpdateWeekdays(val weekdays: List<DayOfWeek>): PlanAction()
    class UpdateGoal(val goal: Goal): PlanAction()
    class RepositionGoal(val goal: Goal, val position: Int): PlanAction()
    class RemoveGoal(val goalId: Long): PlanAction()
    data object StartWorkout: PlanAction()
}
