package com.catscoffeeandkitchen.ui.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.Goal
import com.catscoffeeandkitchen.ui.ExerciseDetailScreen
import com.catscoffeeandkitchen.ui.PlanAction
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen
import com.catscoffeeandkitchen.ui.theme.Spacing
import com.catscoffeeandkitchen.workoutplans.ui.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlanDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: WorkoutPlanDetailViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val focusManager = LocalFocusManager.current

    val state by viewModel.planUiState.collectAsState()

    var exerciseToRemove by remember { mutableStateOf(null as Exercise?) }
    val showExerciseGroupNameDialog by viewModel.showExerciseGroupNameDialog.collectAsState()
    val exercisesToGroup by viewModel.exercisesToGroup.collectAsState()

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

    if (showExerciseGroupNameDialog) {
        val defaultGroupName = stringResource(R.string.exercise_group)

        GroupExercisesDialog(
            exercises = exercisesToGroup,
            onDismiss = { viewModel.hideGroupNameDialog() }
        ) { name ->
            viewModel.addExerciseGroup(
                name ?: defaultGroupName,
                exercisesToGroup
            )
        }
    }

    if (exerciseToRemove != null) {
        AlertDialog(
            onDismissRequest = { exerciseToRemove = null },
            confirmButton = {
                TextButton(onClick = {
                    exerciseToRemove?.let {
                        viewModel.removeGoal(it.id)
                    }
                    exerciseToRemove = null
                }) { Text(stringResource(R.string.remove)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    exerciseToRemove = null
                }) { Text(stringResource(R.string.cancel)) }
            },
            title = {
                Text(
                    exerciseToRemove?.let {
                        stringResource(id = R.string.remove_x_confirm, it.name)
                    } ?: stringResource(R.string.remove_exercise_confirm),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .padding(vertical = Spacing.Default)
    ) {
        if (state.isLoading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        state.error?.let { error ->
            item {
                Text(
                    error.localizedMessage
                        ?: stringResource(id = R.string.plan_error),
                    modifier = Modifier.padding(Spacing.Default)
                )
            }
        }

        state.plan?.let { plan ->
            planItems(
                plan = plan,
                onOpenExercise = {
                    viewModel.waitForGoalUpdate(it)

                    focusManager.clearFocus(force = true)
                    it.exercise?.id?.let { exerciseId ->
                        navController.navigate(
                            LiftingLogScreen.ExerciseDetailScreen.routeWithArgs("$exerciseId")
                        )
                    }
                },
                onOpenGroup = {
                    navController.navigate(
                        LiftingLogScreen.ExerciseGroupDetailScreen()
                            .routeWithArgs(it.toString())
                    )
                },
                onPlanAction = { action ->
                    when (action) {
                        PlanAction.AddExercise -> {
                            navController.navigate(LiftingLogScreen.SearchExercisesScreen.route)
                        }

                        PlanAction.AddExerciseGroup -> {
                            navController.navigate("${LiftingLogScreen.ExerciseGroupListScreen.route}?selectable=true")
                        }

                        is PlanAction.RemoveExercise -> {
                            exerciseToRemove = action.exercise
                        }

                        PlanAction.StartWorkout -> {
                            viewModel.createWorkoutFromPlan()
                            navController.navigate(LiftingLogScreen.WorkoutsScreen.route)
                        }

                        is PlanAction.UpdateWeekdays -> {
                            viewModel.updateWeekdays(action.weekdays)
                        }

                        is PlanAction.UpdateWorkoutName -> {
                            viewModel.updateWorkoutName(action.name)
                        }

                        is PlanAction.UpdateWorkoutNotes -> {
                            viewModel.updateWorkoutNotes(action.notes)
                        }

                        is PlanAction.UpdateGoal -> {
                            viewModel.updateGoal(action.goal)
                        }

                        is PlanAction.RepositionGoal -> {
                            viewModel.repositionGoal(action.goal, action.position)
                        }

                        is PlanAction.RemoveGoal -> {
                            viewModel.removeGoal(action.goalId)
                        }
                    }
                }
            )
        }
    }
}

