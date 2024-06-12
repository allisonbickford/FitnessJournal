package com.catscoffeeandkitchen.ui.groups.detail

import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup


sealed class GroupAction {
    data class UpdateGroupName(val group: ExerciseGroup): GroupAction()
    data class UpdateGroupExercises(
        val group: ExerciseGroup,
        val exerciseNames: List<String>
    ): GroupAction()

    data class RemoveExercise(
        val group: ExerciseGroup,
        val exercise: Exercise
    ): GroupAction()

    data object NavigateToAddExercise: GroupAction()
}