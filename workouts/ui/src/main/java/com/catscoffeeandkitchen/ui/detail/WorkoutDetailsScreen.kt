package com.catscoffeeandkitchen.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.detail.exercise.ExerciseAction
import com.catscoffeeandkitchen.ui.detail.exercise.ExerciseNavigationAction
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen
import com.catscoffeeandkitchen.ui.services.TimerService
import timber.log.Timber

@Composable
fun WorkoutDetailsScreen(
    navController: NavController,
    onStartTimer: (Long, Long) -> Unit,
    timerService: TimerService?,
    modifier: Modifier = Modifier,
    viewModel: WorkoutDetailsViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
                    viewModel.collectSavedState(handle)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
    ) {
        val uiState by viewModel.uiState.collectAsState()
        val unit by viewModel.weightUnit.collectAsState(initial = WeightUnit.Pounds)
        val timers by viewModel.timers.collectAsState(initial = emptyList())

        uiState.exception?.let { error ->
            Column(modifier = Modifier.fillMaxSize()) {
                Text(error.message.toString ())
            }
        }

        if (uiState.isLoading) {
            Column(modifier = Modifier.fillMaxSize()) {
                LinearProgressIndicator(
                    modifier = modifier.fillMaxWidth()
                )
            }
        }

        uiState.workout?.let { workout ->
            WorkoutDetailsContent(
                workout = workout,
                plan = uiState.plan,
                unit = unit,
                timers = timers,
                timerService = timerService,
                personalBests = uiState.personalBests,
                onWorkoutAction = { action ->
                    Timber.d("onWorkoutAction action = $action")
                    when (action) {
                        WorkoutAction.CreatePlanFromWorkout -> {
                            viewModel.createPlanFromWorkout(workout)
                            navController.navigate(LiftingLogScreen.WorkoutPlansScreen.route)
                        }
                        WorkoutAction.Finish -> {
                            viewModel.finishWorkout(workout)
                            navController.popBackStack()
                        }
                        is WorkoutAction.UpdateName -> {
                            Timber.d("onWorkoutAction updating name = ${action.name}")
                            viewModel.updateWorkout(
                                workout.copy(name = action.name))
                        }
                        is WorkoutAction.UpdateNote -> {
                            Timber.d("onWorkoutAction updating note = ${action.note}")
                            viewModel.updateWorkout(
                                workout.copy(note = action.note))
                        }
                    }
                },
                onExerciseAction = { action ->
                    when (action) {
                        is ExerciseAction.AddEntry -> {
                            viewModel.addEntry(
                                workout.id,
                                action.entry
                            )
                        }
                        is ExerciseAction.AddEntryWithExerciseName -> {
                            viewModel.addEntryWithExerciseName(
                                workout,
                                action.name
                            )
                        }
                        is ExerciseAction.MoveEntryTo -> {
                            viewModel.moveEntryTo(
                                workout.id,
                                action.entry,
                                action.newPosition
                            )
                        }
                        is ExerciseAction.RemoveEntry -> {
                            viewModel.removeEntry(action.entry)
                        }
                        is ExerciseAction.ReplaceWithGroup -> {
                            viewModel.replaceWithGroup(action.entry)
                        }
                        is ExerciseAction.SelectExerciseFromGroup -> {
                            viewModel.setExercise(
                                action.entry.position,
                                action.exercise.name
                            )
                        }
                        is ExerciseAction.SwapExercise -> {
                            viewModel.setExercise(
                                action.exercisePosition,
                                action.exercise.name
                            )
                        }
                        is ExerciseAction.UpdateEntry -> {
                            viewModel.updateEntry(workout.id, action.entry)
                        }
                        is ExerciseAction.UpdateSet -> {
                            viewModel.updateSet(action.entryId, action.set)
                        }
                        is ExerciseAction.UpdateSets -> {
                            viewModel.updateSets(action.entryId, action.sets)
                        }
                        is ExerciseAction.AddWarmupSets -> {
                            viewModel.addWarmupSets(action.entry, action.unit)
                        }
                        is ExerciseAction.AddSet -> {
                            viewModel.addSet(action.entry)
                        }
                        is ExerciseAction.RemoveSet -> {
                            viewModel.removeSet(action.set.id)
                        }
                    }
                },
                onNavigationAction = { action ->
                    when (action) {
                        ExerciseNavigationAction.AddExercise -> {
                            navController.navigate(
                                LiftingLogScreen.SearchExercisesScreen.route)
                        }
                        ExerciseNavigationAction.AddExerciseGroup -> { } // no-op
                        is ExerciseNavigationAction.EditGroup -> {
                            viewModel.editingGroup.value = action.group
                            navController.navigate(
                                "${LiftingLogScreen.SearchExercisesMultiSelectScreen.route}?" +
                                        "selectedExercises=${action.group.exercises.joinToString("|") { it.name }}")
                        }
                        is ExerciseNavigationAction.SwapExerciseAt -> {
                            val entry = workout.entries
                                .firstOrNull { it.position == action.position }

                            navController.currentBackStackEntry
                                ?.savedStateHandle?.set("swappingExercise", entry?.position)
                            navController.navigate(
                                "${LiftingLogScreen.SearchExercisesScreen.route}?" +
                                        "category=${entry?.exercise?.category}&" +
                                        "muscle=${entry?.exercise?.musclesWorked
                                            ?.firstOrNull().orEmpty()}"
                            )
                        }
                    }
                },
                onStartTimer = { seconds ->
                    onStartTimer(
                        workout.id,
                        seconds
                    )
                },
            )
        }
    }
}
