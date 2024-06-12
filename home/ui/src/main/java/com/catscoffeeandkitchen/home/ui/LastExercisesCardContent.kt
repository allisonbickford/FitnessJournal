package com.catscoffeeandkitchen.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WorkoutEntry
import com.catscoffeeandkitchen.ui.components.Shimmer
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import java.time.OffsetDateTime

@Composable
fun LastExercisesCardContent(
    state: HomeViewModel.ResultState<List<WorkoutEntry>>
) {
    Text(
        stringResource(R.string.last_exercises),
        style = MaterialTheme.typography.titleLarge
    )

    if (state.isLoading) {
        LastExercisesLoadingShimmer()
    } else if (state.error != null) {
        Text(
            state.error.localizedMessage
                ?: stringResource(R.string.last_exercises_error)
        )
    } else if (state.result != null) {
        state.result
            .filter { it.exercise != null }
            .groupBy { it.exercise?.name }
            .entries
            .forEach { entry ->
                Text(
                    entry.key.orEmpty(),
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    "${entry.value.sumOf { 
                        it.sets.count { set -> set.completedAt != null } 
                    }}x${entry.value.maxOf { it.averageReps }}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = Spacing.Half)
                )
            }
    } else {
        Text(
            stringResource(R.string.no_last_exercises)
        )
    }
}


@Composable
private fun LastExercisesLoadingShimmer() {
    val density = LocalDensity.current

    Shimmer(
        containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
        shimmerColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .height(
                with(density) {
                    MaterialTheme.typography.labelMedium.fontSize.toDp()
                }
            )
            .fillMaxWidth(.4f)
    )

    Spacer(modifier = Modifier.padding(bottom = 2.dp))

    Shimmer(
        containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
        shimmerColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .height(
                with(density) {
                    MaterialTheme.typography.labelMedium.fontSize.toDp()
                }
            )
            .fillMaxWidth(.2f)
    )

    Spacer(modifier = Modifier.padding(bottom = Spacing.Half))

    Shimmer(
        containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
        shimmerColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .height(
                with(density) {
                    MaterialTheme.typography.labelMedium.fontSize.toDp()
                }
            )
            .fillMaxWidth(.5f)
    )

    Spacer(modifier = Modifier.padding(bottom = 2.dp))

    Shimmer(
        containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
        shimmerColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .height(
                with(density) {
                    MaterialTheme.typography.labelMedium.fontSize.toDp()
                }
            )
            .fillMaxWidth(.2f)
    )

}


@Preview
@Composable
fun LastExercisesCardContentPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            LastExercisesCardContent(
                state = HomeViewModel.ResultState(
                    result = listOf(
                        WorkoutEntry(
                            id = 1L,
                            position = 1,
                            group = null,
                            exercise = Exercise("Bicep Curl"),
                            sets = (1..3).toList().map { number ->
                                ExerciseSet(
                                    id = number.toLong(),
                                    setNumber = number,
                                    reps = 10,
                                    weightInPounds = 10f,
                                    weightInKilograms = 10f,
                                    completedAt = OffsetDateTime.now().minusDays(1L)
                                )
                            }
                        ),
                        WorkoutEntry(
                            id = 2L,
                            position = 2,
                            group = null,
                            exercise = Exercise("Inverse Row"),
                            sets = (1..3).toList().map { number ->
                                ExerciseSet(
                                    id = number.toLong(),
                                    setNumber = number,
                                    reps = 10,
                                    completedAt = OffsetDateTime.now().minusDays(1L)
                                )
                            }
                        )
                    )
                )
            )
        }
    }
}

@Preview
@Composable
fun LastExercisesCardContentLoadingPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            LastExercisesCardContent(
                state = HomeViewModel.ResultState(isLoading = true)
            )
        }
    }
}

