package com.catscoffeeandkitchen.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.ui.components.LLButton
import com.catscoffeeandkitchen.ui.search.create.CreateOrChangeExerciseDialog
import com.catscoffeeandkitchen.ui.theme.Spacing
import com.patrykandpatrick.vico.core.draw.drawContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchExercisesMultiSelectScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    muscle: String? = null,
    category: String? = null,
    viewModel: SelectExercisesViewModel = hiltViewModel(),
    ) {
    val searchState = viewModel.search.collectAsState(
        initial = ExerciseSearch(
            muscle = muscle, category = category
        )
    )
    val pagingItems = viewModel.pagedExerciseFlow.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

    val selectedItems by viewModel.selectedExercises.collectAsState(emptyList())
    var showCreateExerciseDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf(null as Exercise?) }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(coroutineScope) {
        if (pagingItems.itemSnapshotList.isEmpty()) {
            viewModel.searchExercises(searchState.value)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (selectedItems.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        navController.previousBackStackEntry?.savedStateHandle?.set<String>(
                            "selectedExercises",
                            selectedItems.joinToString("|") { it.name }
                        )
                        navController.popBackStack()
                    },
                    modifier = Modifier
                ) {
                    Icon(Icons.Default.Check, contentDescription = "finished selecting")
                }
            }
        }
    ) { padding ->
        if (showCreateExerciseDialog) {
            CreateOrChangeExerciseDialog(
                isCreating = editingExercise == null,
                currentExercise = editingExercise ?: Exercise(
                    name = (searchState.value).name.orEmpty().lowercase(),
                    musclesWorked = emptyList()
                ),
                onDismiss = { showCreateExerciseDialog = false },
                onConfirm = { exercise ->
                    if (editingExercise == null) {
                        viewModel.createExercise(exercise)
                    } else {
                        viewModel.updateExercise(exercise)
                    }
                    showCreateExerciseDialog = false
                }
            )
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier.padding(padding)
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

                if (selectedItems.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(2.dp)
                        ) {
                            Text(
                                "${selectedItems.size} exercises selected",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            items(selectedItems) { exercise ->
                SelectableExerciseItem(
                    exercise,
                    isSelected = selectedItems.contains(exercise),
                    onTap = {
                        viewModel.unselectExercise(exercise)
                    },
                    onLongPress = {
                        editingExercise = exercise
                        showCreateExerciseDialog = true
                    }
                )
            }

            items(pagingItems.itemCount) { index ->
                val exercise = pagingItems[index]
                if (exercise != null && !selectedItems.contains(exercise)) {
                    SelectableExerciseItem(
                        exercise,
                        isSelected = selectedItems.contains(exercise),
                        onTap = {
                            if (selectedItems.contains(exercise)) {
                                viewModel.unselectExercise(exercise)
                            } else {
                                viewModel.createExercise(exercise)
                                viewModel.selectExercise(exercise)
                            }
                        },
                        onLongPress = {
                            editingExercise = exercise
                            showCreateExerciseDialog = true
                        }
                    )
                }
            }

            when (val loadState = pagingItems.loadState.refresh) {
                is LoadState.Error -> {
                    item {
                        HorizontalDivider()
                        Text(loadState.error.message.toString())
                    }
                }
                else -> {}
            }

            if (pagingItems.loadState.refresh is LoadState.NotLoading) {
                item {
                    LLButton(
                        onClick = {
                            editingExercise = null
                            showCreateExerciseDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Exercise")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableExerciseItem(
    exercise: Exercise,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { onTap() },
                onLongClick = { onLongPress() }
            ),
        colors = if (isSelected) CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) else CardDefaults.cardColors()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    exercise.name,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
    //            Text(
    //                "Performed ${exercise} sets",
    //                modifier = Modifier.padding(2.dp),
    //                style = MaterialTheme.typography.labelSmall
    //            )
                exercise.category?.let {
                    Text(it.name, style = MaterialTheme.typography.labelLarge)
                }

                if (exercise.musclesWorked.isNotEmpty()) {
                    Text(exercise.musclesWorked.joinToString(", ") { it })
                }
        }
        }
    }
}

@Preview
@Composable
fun SelectableExerciseItemPreview() {
    SelectableExerciseItem(
        isSelected = true,
        exercise = Exercise(
            name = "Bicep Curl",
            musclesWorked = listOf("Biceps", "Triceps"),
        )
    )
}
