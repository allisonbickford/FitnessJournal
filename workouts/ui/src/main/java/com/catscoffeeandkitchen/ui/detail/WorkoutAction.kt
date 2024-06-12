package com.catscoffeeandkitchen.ui.detail

sealed class WorkoutAction {
    class UpdateName(val name: String): WorkoutAction()
    class UpdateNote(val note: String?): WorkoutAction()
    data object Finish: WorkoutAction()
    data object CreatePlanFromWorkout: WorkoutAction()
}
