package com.catscoffeeandkitchen.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.catscoffeeandkitchen.models.Workout
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen
import timber.log.Timber

@Composable
fun WorkoutsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: WorkoutsViewModel = hiltViewModel(),
) {
    val workouts = viewModel.pagedWorkouts.collectAsLazyPagingItems()
    var workoutToDelete by remember { mutableStateOf(null as Workout?) }

    if (workoutToDelete != null) {
        AlertDialog(
            onDismissRequest = { workoutToDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    workoutToDelete?.let { viewModel.deleteWorkout(it) }
                    workoutToDelete = null
                }) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { workoutToDelete = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Remove ${workoutToDelete?.name.orEmpty().ifEmpty { "workout" }}?") },
        )
    }

    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
    ) {
        items(workouts.itemCount) { index ->
            workouts[index]?.let { item ->
                if (index == 0) {
                    HorizontalDivider()
                }

                WorkoutSummaryItem(
                    item,
                    onClick = {
                        Timber.d("Navigating to ${item.id}")
                        navController.navigate(
                            "${LiftingLogScreen.WorkoutDetails.route}/${item.id}"
                        )
                    },
                    onLongPress = { workoutToDelete = item }
                )

                HorizontalDivider()
            }
        }
    }
}

@Preview
@Composable
fun WorkoutsScreenPreview() {
    WorkoutsScreen(
        navController = rememberNavController()
    )
}
