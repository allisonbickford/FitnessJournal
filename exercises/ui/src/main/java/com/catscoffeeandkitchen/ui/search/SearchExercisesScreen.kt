package com.catscoffeeandkitchen.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.exercises.ui.R
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.ui.components.LLButton
import com.catscoffeeandkitchen.ui.muscleStrings
import com.catscoffeeandkitchen.ui.search.create.CreateOrChangeExerciseDialog
import com.catscoffeeandkitchen.ui.theme.Spacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchExercisesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    muscle: String? = null,
    category: String? = null,
    viewModel: SearchExercisesViewModel = hiltViewModel(),
    ) {
    val searchState = viewModel.search.collectAsState(
        initial = ExerciseSearch(
            muscle = muscle, category = category
        )
    )
    val pagingItems = viewModel.pagedExerciseFlow.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    var showCreateExerciseDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf(null as Exercise?) }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(coroutineScope) {
        if (pagingItems.itemSnapshotList.isEmpty()) {
            viewModel.searchExercises(searchState.value)
        }
    }

    if (showCreateExerciseDialog) {
        CreateOrChangeExerciseDialog(
            isCreating = editingExercise == null,
            currentExercise = editingExercise ?:
                Exercise(
                    name = searchState.value.name.orEmpty().lowercase(),
                    musclesWorked = emptyList()
                ),
            onDismiss = { showCreateExerciseDialog = false },
            onConfirm = { exercise ->
                if  (editingExercise == null) {
                    viewModel.createExercise(exercise)
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "exerciseToAdd",
                        exercise.name
                    )
                    navController.popBackStack()
                } else {
                    viewModel.updateExercise(exercise)
                }
                showCreateExerciseDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        stickyHeader {
            val outlineColor = MaterialTheme.colorScheme.outlineVariant

            SearchExerciseHeader(
                currentSearch = searchState.value.name,
                categoryFilter = searchState.value.category,
                muscleFilter = searchState.value.muscle,
                onSearch = { text ->
                    viewModel.searchExercises(searchState.value.copy(name = text))
                    pagingItems.refresh()
                },
                filterMuscle = { muscle ->
                    viewModel.searchExercises(searchState.value.copy(muscle = muscle))
                    pagingItems.refresh()
                },
                filterCategory = { category ->
                    viewModel.searchExercises(searchState.value.copy(category = category))
                    pagingItems.refresh()
                },
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = outlineColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )
                }
            )

            (pagingItems.loadState.refresh as? LoadState.Loading)?.let {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        items(pagingItems.itemCount) {index ->
            val exercise = pagingItems[index]
            if (exercise != null) {
                ExerciseItem(exercise,
                    onClick = {
                        viewModel.createExercise(exercise)
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("exerciseToAdd", exercise.name)
                        navController.popBackStack()
                    },
                )

                HorizontalDivider()
            }
        }

        (pagingItems.loadState.append as? LoadState.Error)?.let { state ->
            item {
                HorizontalDivider()
                Text(state.error.message.toString())
            }
        }

        if (pagingItems.loadState.append is LoadState.NotLoading) {
            item {
                LLButton(
                    onClick = {
                        editingExercise = null
                        showCreateExerciseDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.Default)
                ) {
                    Text(stringResource(R.string.create_exercise))
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    ListItem(
        leadingContent = {
            exercise.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Exercise Image",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(6.dp),
                    colorFilter = if (darkTheme)
                        ColorFilter.colorMatrix(
                            ColorMatrix(floatArrayOf(
                                -1f, 0f, 0f, 0f, 255f,
                                0f, -1f, 0f, 0f, 255f,
                                0f, 0f, -1f, 0f, 255f,
                                0f, 0f, 0f, 1f, 0f
                            ))
                        ) else null
                )
            }
        },
        headlineContent = {
            Text(
                exercise.name,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        supportingContent = {
            exercise.category?.let { Text(it.name, style = MaterialTheme.typography.labelLarge) }

            if (exercise.musclesWorked.isNotEmpty()) {
                Text(
                    exercise.musclesWorked
                        .map { muscleStrings(it) }
                        .joinToString(", ")
                )
            }
        },
        modifier = modifier.clickable {
            onClick()
        }
    )
}

@Preview
@Composable
fun ExerciseItemPreview() {
    ExerciseItem(
        Exercise(
            name = "Bicep Curl",
            musclesWorked = listOf("Biceps", "Triceps"),
        ),
        onClick = { }
    )
}
