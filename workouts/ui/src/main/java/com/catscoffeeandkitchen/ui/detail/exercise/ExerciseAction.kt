package com.catscoffeeandkitchen.ui.detail.exercise

import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.WorkoutEntry

sealed class ExerciseAction {
    class AddEntryWithExerciseName(val name: String): ExerciseAction()
    class SwapExercise(val exercisePosition: Int, val exercise: Exercise): ExerciseAction()
    class RemoveEntry(val entry: WorkoutEntry): ExerciseAction()
    class MoveEntryTo(val entry: WorkoutEntry, val newPosition: Int): ExerciseAction()
    class ReplaceWithGroup(val entry: WorkoutEntry): ExerciseAction()
    class SelectExerciseFromGroup(
        val exercise: Exercise,
        val entry: WorkoutEntry
    ): ExerciseAction()
    class UpdateSet(val entryId: Long, val set: ExerciseSet): ExerciseAction()
    class UpdateSets(val entryId: Long, val sets: List<ExerciseSet>): ExerciseAction()
    class UpdateEntry(val entry: WorkoutEntry): ExerciseAction()
    class AddEntry(val entry: WorkoutEntry, val workoutId: Long): ExerciseAction()
    class AddSet(val entry: WorkoutEntry): ExerciseAction()

    class AddWarmupSets(val entry: WorkoutEntry, val unit: WeightUnit): ExerciseAction()
    class RemoveSet(val set: ExerciseSet) : ExerciseAction()
}
