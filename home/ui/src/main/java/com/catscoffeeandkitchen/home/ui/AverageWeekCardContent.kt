package com.catscoffeeandkitchen.home.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.WorkoutWeekStats
import com.catscoffeeandkitchen.ui.components.Shimmer
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import java.lang.Exception
import java.lang.NullPointerException
import java.time.DayOfWeek
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun AverageWeekCardContent(
    state: HomeViewModel.ResultState<WorkoutWeekStats>,
) {
    Text(
        text = stringResource(R.string.average_week),
        style = MaterialTheme.typography.titleLarge
    )

    if (state.error != null) {
        Text(
            state.error.localizedMessage ?: stringResource(R.string.average_week_error)
        )
    } else {
        Crossfade(
            targetState = state.isLoading,
            label = "Crossfade Loading State"
        ) { loading ->
            Column {
                if (loading) {
                    AverageWeekLoadingShimmer()
                } else {
                    if (state.result == null || state.result.mostCommonDays.isEmpty()) {
                        Text(stringResource(R.string.no_average_week))
                    } else {
                        state.result.averageWorkoutsPerWeek?.let { average ->
                            Text(
                                pluralStringResource(
                                    id = R.plurals.workouts_per_week,
                                    count = average.roundToInt(),
                                    average.roundToInt()
                                ),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val mostCommonHour = when {
                            state.result.mostCommonTimes.first() < 13 -> "${state.result.mostCommonTimes.first()}am"
                            else -> "${state.result.mostCommonTimes.first() - 12}pm"
                        }
                        Text(
                            "usually finishing around $mostCommonHour",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )

                        DaysInWeek(
                            state.result.mostCommonDays,
                            modifier = Modifier.padding(top = 12.dp, start = 4.dp)
                        )

                        Text(
                            stringResource(R.string.most_common_days),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DaysInWeek(
    mostCommonDays: List<DayOfWeek>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        DayOfWeek.entries
            .filter { day -> mostCommonDays.isNotEmpty() &&
                    mostCommonDays.any { it == day } }
            .forEach { day ->
                Surface(
                    modifier = Modifier
                        .width(45.dp),
                    shape = MaterialTheme.shapes.small,
                    tonalElevation = 6.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) { Text(
                    when (day) {
                        DayOfWeek.THURSDAY, DayOfWeek.SUNDAY -> day.name.take(2).lowercase()
                        else -> day.name.first().toString()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )}
            }
    }
}

@Composable
private fun AverageWeekLoadingShimmer() {
    val density = LocalDensity.current

    Spacer(modifier = Modifier.padding(bottom = Spacing.Quarter))

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
            .width(120.dp)
    )

    Spacer(modifier = Modifier.padding(bottom = Spacing.Half))
    
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
            .width(160.dp)
    )

    Spacer(modifier = Modifier.padding(bottom = Spacing.Default))

    Row(
        modifier = Modifier.padding(start = Spacing.Quarter)
    ) {
        (1..3).forEach { _ ->
            Shimmer(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                shimmerColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .size(height = 32.dp, width = 40.dp)
            )

            Spacer(modifier = Modifier.padding(end = Spacing.Half))
        }
    }

    Spacer(modifier = Modifier.padding(bottom = Spacing.Quarter))

    Shimmer(
        containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
        shimmerColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .padding(start = Spacing.Quarter)
            .clip(MaterialTheme.shapes.extraSmall)
            .height(
                with(density) {
                    MaterialTheme.typography.bodyMedium.fontSize.toDp()
                }
            )
            .width(80.dp)
    )
}

@Preview
@Composable
private fun AverageWeekCardPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            AverageWeekCardContent(
                HomeViewModel.ResultState(result = WorkoutWeekStats(
                    since = OffsetDateTime.now().minusWeeks(6L),
                    averageWorkoutsPerWeek = 3f,
                    mostCommonDays = listOf(
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.FRIDAY
                    ),
                    mostCommonTimes = listOf(6, 13)
                ))
            )
        }
    }
}

@Preview
@Composable
private fun AverageWeekCardErrorPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            AverageWeekCardContent(
                HomeViewModel.ResultState(error = Exception())
            )
        }
    }
}

@Preview
@Composable
private fun AverageWeekCardLoadingPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            AverageWeekCardContent(
                HomeViewModel.ResultState(
                    isLoading = true,
                    error = null
                )
            )
        }
    }
}

