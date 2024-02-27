package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.catscoffeeandkitchen.ui.list.WorkoutPlanSummaryItem
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen
import com.catscoffeeandkitchen.ui.theme.Spacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectPlanScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    newWorkoutViewModel: NewWorkoutViewModel = hiltViewModel()
) {
    val plans by newWorkoutViewModel.plans.collectAsState(initial = null)
    val createdId by newWorkoutViewModel.createdWorkout.collectAsState(initial = null)

    LaunchedEffect(createdId) {
        if (createdId != null) {
            navController.navigate("${LiftingLogScreen.WorkoutDetails.route}/$createdId") {
                popUpTo(LiftingLogScreen.WorkoutsScreen.route)
            }
        }
    }

    when {
        plans.isNullOrEmpty() -> { CircularProgressIndicator() }
        else -> {
            LazyColumn(
                modifier = modifier.padding(Spacing.Default),
            ) {
                stickyHeader {
                    Text(
                        "Select a Plan",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                items(plans.orEmpty()) { item ->
                    WorkoutPlanSummaryItem(
                        workout = item,
                        onClick = {
                            newWorkoutViewModel.createWorkout(item.id)
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            newWorkoutViewModel.createWorkout()
                        }) {
                            Text("Skip")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SelectPlanScreenPreview() {
    SelectPlanScreen(
        navController = rememberNavController()
    )
}
