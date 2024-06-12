package com.catscoffeeandkitchen.home.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.charts.ExerciseStatsChart
import com.catscoffeeandkitchen.ui.components.Shimmer
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import java.time.OffsetDateTime
import kotlin.math.roundToInt

@Composable
fun MostImprovedExerciseCardContent(
    state: HomeViewModel.ResultState<ExerciseProgressStats>,
    unit: WeightUnit
) {
    Text(
        stringResource(R.string.most_improved_exercise),
        style = MaterialTheme.typography.titleLarge
    )

    if (state.error != null) {
        Text(
            state.error.localizedMessage
                ?: stringResource(R.string.most_improved_exercise_error),
            style = MaterialTheme.typography.titleLarge
        )
    } else {
        Crossfade(
            targetState = state.isLoading,
            label = "Crossfade Loading State"
        ) { loading ->
            Column {
                if (loading) {
                    MostImprovedLoadingShimmer()
                } else {
                    if (state.result != null) {
                        val stats = state.result

                        val best1RM = stats.bestSet?.repMax(unit) ?: 0.0
                        val worst1RM = stats.worstSet?.repMax(unit) ?: 0.0

                        Text(
                            stats.exercise.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(id = R.drawable.trending_up),
                                "Trending Up",
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(end = 8.dp)
                            )

                            Column {
                                Text(
                                    stringResource(
                                        R.string.x_increased_calculated_1rm,
                                        (best1RM - worst1RM).div(worst1RM)
                                            .times(100)
                                            .roundToInt()
                                    ),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    ),
                                    fontWeight = FontWeight.Bold
                                )

                                DurationText(
                                    chronoUnit = stats.chronoUnit,
                                    from = stats.earliestSetCompletedAt ?: OffsetDateTime.now()
                                )
                            }
                        }

                        ExerciseStatsChart(stats = stats, unit = unit)
                    } else {
                        Text(
                            stringResource(R.string.no_improved_exercise),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MostImprovedLoadingShimmer() {
    val density = LocalDensity.current

    Spacer(modifier = Modifier.padding(top = Spacing.Half))

    Shimmer(
        containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
        shimmerColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .height(
                with(density) {
                    MaterialTheme.typography.titleLarge.fontSize.toDp()
                }
            )
            .fillMaxWidth(.4f)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = Spacing.Default)
    ) {
        Icon(
            painterResource(id = R.drawable.trending_up),
            "Trending Up",
            modifier = Modifier
                .size(36.dp)
                .padding(end = 8.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Shimmer(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                shimmerColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .height(
                        with(density) {
                            MaterialTheme.typography.bodyMedium.fontSize.toDp()
                        }
                    )
                    .fillMaxWidth(.5f)
            )

            Shimmer(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                shimmerColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .height(
                        with(density) {
                            MaterialTheme.typography.bodyMedium.fontSize.toDp()
                        }
                    )
                    .fillMaxWidth(.35f)
            )
        }
    }

    Spacer(modifier = Modifier.padding(top = 40.dp))

    Shimmer(
        containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
        shimmerColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .height(height = 150.dp)
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun LoadingShimmerPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            MostImprovedLoadingShimmer()
        }
    }
}


@Preview
@Composable
private fun LoadingShimmerPreviewDark() {
    LiftingLogTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            MostImprovedLoadingShimmer()
        }
    }
}

@Preview
@Composable
private fun MostImprovedExercisePreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            MostImprovedExerciseCardContent(
                state = HomeViewModel.ResultState(
                    result = ExerciseProgressStats(
                        Exercise(name = "Bicep Curl"),
                        sets = listOf(
                            ExerciseSet(
                                id = 1,
                                setNumber = 1,
                                reps = 6,
                                weightInPounds = 6f,
                                completedAt = OffsetDateTime.now().minusMonths(2)
                            ),
                            ExerciseSet(
                                id = 2,
                                setNumber = 2,
                                reps = 7,
                                weightInPounds = 8f,
                                completedAt = OffsetDateTime.now().minusMonths(1)
                            ),
                            ExerciseSet(
                                id = 3,
                                reps = 7,
                                weightInPounds = 12f,
                                completedAt = OffsetDateTime.now().minusWeeks(1)
                            ),
                            ExerciseSet(
                                id = 4,
                                reps = 6,
                                weightInPounds = 15f,
                                completedAt = OffsetDateTime.now().minusDays(3)
                            )
                        )
                    )
                ),
                unit = WeightUnit.Pounds
            )
        }
    }
}

