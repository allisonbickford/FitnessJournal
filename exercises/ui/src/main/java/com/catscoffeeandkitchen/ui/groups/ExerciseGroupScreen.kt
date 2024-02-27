package com.catscoffeeandkitchen.ui.groups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen
import com.catscoffeeandkitchen.ui.theme.Spacing


@Composable
fun ExerciseGroupScreen(
    navController: NavController,
    selectable: Boolean,
    modifier: Modifier = Modifier
) {

    ExerciseGroupScreenContent(
        navController,
        selectable = selectable,
        modifier = modifier
    )
}

@Composable
fun ExerciseGroupScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    viewModel: ExerciseGroupsViewModel = hiltViewModel()
    ) {
    val groupState by viewModel.exerciseGroups.collectAsState()
    var animateUpdate by remember { mutableStateOf<Long?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.let {
            viewModel.collectSavedState(it)
        }

        viewModel.events.collect { event ->
            when (event) {
                is ExerciseGroupsViewModel.GroupEvent.UpdatedGroup -> animateUpdate = event.group.id
                else -> {}
            }
        }
    }



    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = Spacing.Default),
        modifier = modifier
    ) {
        if (groupState.isLoading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        groupState.error?.let {
            item {
                Text(it.localizedMessage ?: "There was an error")
            }
        }

        groupState.groups?.let { groups ->
            items(groups) { group ->
                if (group.id == groups.first().id) {
                    HorizontalDivider()
                }

                ExerciseGroupSummary(
                    group,
                    renameGroup = { newName ->
                        viewModel.renameGroup(group, newName)
                    },
                    editExercises = {
                        viewModel.setEditingGroup(group)
                        navController.navigate(
                            "${LiftingLogScreen.SearchExercisesMultiSelectScreen.route}?" +
                                    "selectedExercises=${group.exercises.joinToString("|") { it.name }}"
                        )
                    },
                    removeGroup = { viewModel.removeGroup(group) },
                    shouldAnimate = animateUpdate == group.id,
                    onAnimationFinished = {
                        animateUpdate = null
                    },
                    onClick = when {
                        selectable -> ({
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selectedGroup",
                                group.id
                            )
                            navController.popBackStack()
                        })
                        else -> ({
                            navController.navigate(
                                "${LiftingLogScreen.SearchExercisesMultiSelectScreen.route}?" +
                                        "selectedExercises=${group.exercises.joinToString("|") { it.name }}"
                            )
                        })
                    }
                )

                HorizontalDivider()
            }
        }
    }

}
