package com.catscoffeeandkitchen.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.catscoffeeandkitchen.ui.navigation.FitnessJournalBottomNavigationBar
import com.catscoffeeandkitchen.ui.navigation.FitnessJournalTopAppBar
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen

@Composable
fun WorkoutPlansScreen(
    navController: NavController,
    viewModel: WorkoutPlansViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            if (event is WorkoutPlansViewModel.PlanEvent.CreatedPlan) {
                navController.navigate(
                    "${LiftingLogScreen.WorkoutPlanEditScreen.route}/${event.id}"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            FitnessJournalTopAppBar(
                title = stringResource(id = com.catscoffeeandkitchen.common.ui.R.string.plans),
                onNavigateToSettings = {
                    navController.navigate(LiftingLogScreen.Settings.route)
                }
            )
        },
        bottomBar = {
            FitnessJournalBottomNavigationBar(navController = navController)
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.createWorkoutPlan()
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "create plan")
            }
        }
    ) { padding ->
        WorkoutPlansScreenContent(
            navController,
            viewModel = viewModel,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun WorkoutPlansScreenContent(
    navController: NavController,
    viewModel: WorkoutPlansViewModel,
    modifier: Modifier = Modifier,
) {

    val state by viewModel.plans.collectAsState()

    LazyColumn(modifier = modifier
        .background(MaterialTheme.colorScheme.background),
    ) {
        if (state.isLoading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        state.error?.let {
            item {
                Text(it.localizedMessage ?: "Error")
            }
        }

        state.plans?.let { plans ->
            items(plans) { item ->
                if (plans.firstOrNull()?.id == item.id) {
                    HorizontalDivider()
                }

                WorkoutPlanSummaryItem(
                    item,
                    onClick = {
                        navController.navigate(
                            "${LiftingLogScreen.WorkoutPlanEditScreen.route}/${item.id}"
                        )
                    }
                )

                HorizontalDivider()
            }
        }
    }
//
//    Timber.d(viewModel.workouts.value.toString())
//        when (val workouts = viewModel.workouts.value) {
//            is DataState.Loading -> {
//                Column(
//                    modifier = modifier
//                        .fillMaxSize()
//                        .background(MaterialTheme.colorScheme.background),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Box(modifier = Modifier.fillMaxSize(.3f)) {
//                        CircularProgressIndicator()
//                    }
//                }
//            }
//            is DataState.Success -> {
//                if (workouts.data.isNotEmpty()) {
//                        LazyColumn(modifier = modifier
//                            .background(MaterialTheme.colorScheme.background)
//                            .testTag(TestTags.ScrollableComponent),
//                        ) {
//                            items(workouts.data) { item ->
//                                WorkoutPlanSummaryCard(
//                                    item,
//                                    onTap = {
//                                        navController.navigate(
//                                            "${LiftingLogScreen.WorkoutPlanEditScreen.route}/${item.id}"
//                                        )
//                                    }
//                                )
//                            }
//                        }
//                } else {
//                    EmptyWorkoutList(
//                        modifier = modifier,
//                    )
//                }
//            }
//            is DataState.Error -> {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(MaterialTheme.colorScheme.background),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Text(workouts.e.message ?: "unknown error")
//                }
//            }
//            is DataState.NotSent -> {}
//        }
}

@Preview
@Composable
fun WorkoutPlansScreenPreview() {
    WorkoutPlansScreen(
        navController = rememberNavController()
    )
}
