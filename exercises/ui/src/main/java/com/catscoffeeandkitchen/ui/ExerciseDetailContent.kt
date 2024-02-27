package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.exercises.ui.R
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun ExerciseDetailContent(
    id: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: ExerciseDetailViewModel = hiltViewModel<
            ExerciseDetailViewModel,
            ExerciseDetailViewModel.Factory>(key = id.toString()) { factory ->
                factory.create(id)
            }

    val state by viewModel.exercise.collectAsState()

    Column(
        modifier = modifier
            .padding(Spacing.Default)
    ) {
        state.exercise?.let { exercise ->
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.headlineSmall
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxWidth(.5f)
                .height(
                    with(LocalDensity.current) {
                        MaterialTheme.typography.headlineSmall.lineHeight.toDp()
                    }
                )
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = .2f),
                            MaterialTheme.colorScheme.onSurface.copy(alpha = .4f)
                        )
                    )
                )
        )

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        state.exercise?.let { exercise ->
            exercise.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Depiction of ${exercise.name}",
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }

            val labels = stringArrayResource(R.array.search_exercise_category).toList()
            Row(
                modifier = Modifier.padding(Spacing.Default),
            ) {
                Column(
                    modifier = Modifier
                        .padding(end = Spacing.Default)
                        .weight(1f)
                ) {
                    exercise.category?.let { category ->
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    exercise.musclesWorked.forEach { muscle ->
                        Text(
                            muscle,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                RadarChart(
                    chartData = RadarChartData(
                        labels = labels,
                        levels = 4,
                        data = convertExerciseToMuscleLevels(exercise.musclesWorked).map { pair ->
                            RadarChartPoint(
                                labels.indexOf(pair.first),
                                pair.second
                            )
                        }
                    ),
                    labelStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(.65f)
                )
            }
        }
    }
}

private fun convertExerciseToMuscleLevels(
    musclesWorked: List<String>
): List<Pair<String?, Int>> {
    return musclesWorked
        .groupBy { muscle ->
            MuscleCategory.entries.firstOrNull { it.muscles.contains(muscle) }?.name
        }
        .filterKeys { it != null }
        .map { entry ->
            entry.key to (entry.value.size * 2).coerceAtMost(4)
        }
        .filter { it.second > 0 }
}
