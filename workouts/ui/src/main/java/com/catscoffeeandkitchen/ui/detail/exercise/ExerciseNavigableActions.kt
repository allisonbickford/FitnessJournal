package com.catscoffeeandkitchen.ui.detail.exercise

import com.catscoffeeandkitchen.models.ExerciseGroup

sealed class ExerciseNavigationAction {
    data object AddExercise: ExerciseNavigationAction()
    data object AddExerciseGroup: ExerciseNavigationAction()
    class SwapExerciseAt(val position: Int): ExerciseNavigationAction()
    class EditGroup(val group: ExerciseGroup): ExerciseNavigationAction()
}
