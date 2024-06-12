package com.catscoffeeandkitchen.ui

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.catscoffeeandkitchen.exercises.youtube_player.YouTubeHorizontalPager
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.charts.ExerciseStatsChart
import com.catscoffeeandkitchen.ui.components.Shimmer
import com.catscoffeeandkitchen.ui.detail.ExerciseFullWidthImage
import com.catscoffeeandkitchen.ui.detail.ExerciseInstructions
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navigateBack: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val unit by viewModel.unit.collectAsState()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(state.exercise?.name.orEmpty())
                },
                navigationIcon = {
                    IconButton(onClick = { navigateBack(null) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Go Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        ScrollableExerciseDetail(
            state = state,
            unit = unit,
            listState = listState,
            modifier = modifier.padding(paddingValues),
            selectExercise = { viewModel.addAndSelectExercise(it) },
            replaceExercise = {
                state.exercise?.let {
                    navigateBack(it.id)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScrollableExerciseDetail(
    state: ExerciseDetailViewModel.ExerciseUIState,
    unit: WeightUnit,
    listState: LazyListState,
    selectExercise: (Exercise) -> Unit,
    replaceExercise: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(vertical = Spacing.Default),
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    ) {
        if (state.isNotOriginal) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { replaceExercise() }) {
                        Text("Replace ${state.originalExercise?.name}")
                    }
                }
            }
        }

        if (state.isLoading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            ExerciseFullWidthImage(
                imageUrl = state.exercise?.imageUrl,
                contentDescription = "Depiction of ${state.exercise?.name}"
            )
        }

        state.exercise?.let { exercise ->
            item {
                exercise.instructions?.let { instruction ->
                    ExerciseInstructions(instruction)
                }
            }

            if (state.stats?.sets.orEmpty().isNotEmpty()) {
                item {
                    ExerciseStatsChart(
                        stats = state.stats ?: ExerciseProgressStats(exercise),
                        unit = unit
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = !state.cues.isNullOrEmpty(),
                    modifier = Modifier.animateItemPlacement()
                ) {
                    Column {
                        Text(
                            "Cues",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(start = Spacing.Default)
                        )

                        state.cues.orEmpty().forEach { cue ->
                            Text(
                                cue,
                                modifier = Modifier.padding(start = Spacing.Double)
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.animateItemPlacement()
                ) {
                    Text(
                        "Videos",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = Spacing.Default)
                    )
                }

                YouTubeHorizontalPager(
                    search = "${exercise.name} Exercise",
                    startWithVolumeOn = false
                )

                Spacer(modifier = Modifier.padding(top = Spacing.Half))
            }

            item {
                AnimatedVisibility(
                    visible = state.similarExercises.isNotEmpty(),
                    enter = expandVertically(),
                    modifier = Modifier.animateItemPlacement()
                ) {
                    Column {
                        Text(
                            "Similar Exercises",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(start = Spacing.Default)
                        )
                        SimilarExercisesRow(
                            exercises = state.similarExercises,
                            onClickExercise = { selectExercise(it) },
                            modifier = Modifier.padding(vertical = Spacing.Half)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailLoadingPreview() {
    LiftingLogTheme {
        Scaffold { padding ->
            ScrollableExerciseDetail(
                ExerciseDetailViewModel.ExerciseUIState(
                    isLoading = true
                ),
                unit = WeightUnit.Pounds,
                selectExercise = { },
                replaceExercise = { },
                listState = rememberLazyListState(),
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailErrorPreview() {
    LiftingLogTheme {
        Scaffold { padding ->
            ScrollableExerciseDetail(
                ExerciseDetailViewModel.ExerciseUIState(
                    error = Exception("This exercise could not be found.")
                ),
                unit = WeightUnit.Pounds,
                selectExercise = { },
                replaceExercise = { },
                listState = rememberLazyListState(),
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailPreview() {
    LiftingLogTheme {
        Scaffold { padding ->
            ScrollableExerciseDetail(
                ExerciseDetailViewModel.ExerciseUIState(
                    exercise = Exercise(
                        name = "Bicep Curl",
                        id = 1,
                        musclesWorked = listOf("Bicep"),
                        category = MuscleCategory.Arms,
                        equipment = listOf(EquipmentType.Dumbell),
                        instructions = "The bicep curl is a targeted exercise performed by " +
                                "bringing dumbbells up from a straight arm position to a " +
                                "folded arm position."
                    ),
                    similarExercises = listOf(
                        Exercise(
                            name = "Hammer Curl",
                            id = 2
                        ),
                        Exercise(
                            name = "Push Up",
                            id = 2
                        )
                    )
                ),
                selectExercise = { },
                replaceExercise = { },
                unit = WeightUnit.Pounds,
                listState = rememberLazyListState(),
                modifier = Modifier.padding(padding),
            )
        }
    }
}
