package com.catscoffeeandkitchen.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.models.Workout
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.models.WorkoutPlan
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.components.LLButton
import com.catscoffeeandkitchen.ui.components.LLOutlinedButton
import com.catscoffeeandkitchen.ui.detail.exercise.ExerciseAction
import com.catscoffeeandkitchen.ui.detail.exercise.ExerciseItem
import com.catscoffeeandkitchen.ui.detail.exercise.ExerciseNavigationAction
import com.catscoffeeandkitchen.ui.services.TimerService
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import timber.log.Timber
import java.time.OffsetDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkoutDetailsContent(
    workout: Workout,
    plan: WorkoutPlan?,
    unit: WeightUnit,
    timers: List<Long>,
    timerService: TimerService?,
    personalBests: List<WorkoutEntry>,
    onWorkoutAction: (WorkoutAction) -> Unit,
    onExerciseAction: (ExerciseAction) -> Unit,
    onNavigationAction: (ExerciseNavigationAction) -> Unit,
    onStartTimer: (Long) -> Unit = {}
) {
    val (startTimerOnSetFinish, setTimerOnStartFinish) = rememberSaveable { mutableStateOf(false) }
    val (selectedTimer, setSelectedTimer) = remember { mutableLongStateOf(30L) }
    val secondsOnTimer = timerService?.secondsFlow?.collectAsState(initial = null)

    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier
    ) {
        item {
            WorkoutNameAndNoteSection(
                workoutName = workout.name,
                workoutNote = workout.note,
                onUpdateName = { onWorkoutAction(WorkoutAction.UpdateName(it)) },
                onUpdateNote = { onWorkoutAction(WorkoutAction.UpdateNote(it)) },
            )
        }

        item {
            AnimatedVisibility(
                visible = workout.completedAt != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                LLButton(
                    onClick = { onWorkoutAction(WorkoutAction.CreatePlanFromWorkout) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.create_plan_from_this_workout),
                    )
                }
            }
        }

        if (workout.completedAt == null) {
            stickyHeader {
                TimerSection(
                    timers = timers,
                    secondsLeft = secondsOnTimer?.value,
                    selectedTimer = selectedTimer,
                    autoStartTimer = startTimerOnSetFinish,
                    onToggleAutoStartTimer = { setTimerOnStartFinish(!startTimerOnSetFinish) },
                    onUpdateSelectedTimer = setSelectedTimer,
                    onStartTimer = onStartTimer
                )
            }
        }

        itemsIndexed(workout.entries, key = { _, entry -> "entry_${entry.id}" }) { index, entry ->
            if (index == 0) {
                HorizontalDivider()
            }

            ExerciseItem(
                entry,
                goal = plan?.goals?.firstOrNull { it.position == entry.position },
                unit = unit,
                isFirstExercise = index == 0,
                isLastExercise = index == workout.entries.lastIndex,
                personalBest = personalBests
                    .firstOrNull { it.exercise != null && it.exercise?.id == entry.exercise?.id },
                onExerciseAction = onExerciseAction,
                onNavigationAction = onNavigationAction,
                onCompleteSet = { time ->
                    if (startTimerOnSetFinish && time != null) {
                        Timber.d("Auto-starting timer for $selectedTimer seconds")
                        onStartTimer(selectedTimer)
                    } else {
                        Timber.d("Auto-Start Timer = $startTimerOnSetFinish")
                        Timber.d("Set was completed = ${time != null}")
                    }
                },
                modifier = Modifier.animateItemPlacement()
            )

            HorizontalDivider()
        }

        if (workout.completedAt == null) {
            item {
                LLButton(
                    onClick = {
                        onNavigationAction(ExerciseNavigationAction.AddExercise)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.Default)
                ) {
                    Text(stringResource(R.string.add_exercise))
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.Default),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LLOutlinedButton(
                        onClick = { onWorkoutAction(WorkoutAction.Finish) },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(horizontal = Spacing.Default)
                    ) {
                        Text(stringResource(R.string.finish_workout))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutDetailsPreview() {
    val workoutState = Workout(
        id = 1L,
//        completedAt = OffsetDateTime.now().minusDays(3),
        addedAt = OffsetDateTime.now().minusDays(10),
        entries = listOf(
            WorkoutEntry(
                exercise = Exercise(name = "Bicep Curl"),
                sets = listOf(
                    ExerciseSet(
                        id = 2,
                        setNumber = 1,
                        reps = 10
                    )
                ),
                group = null,
                position = 1
            )
        )
    )

    LiftingLogTheme {
        WorkoutDetailsContent(
            workout = workoutState,
            plan = null,
            unit = WeightUnit.Pounds,
            timers = listOf(30L, 60L, 90L, 120L, 180L),
            timerService = null,
            personalBests = emptyList(),
            onWorkoutAction = { },
            onExerciseAction = { },
            onNavigationAction = { },
        )
    }
}
