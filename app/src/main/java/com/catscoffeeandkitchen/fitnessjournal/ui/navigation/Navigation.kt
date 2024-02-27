package com.catscoffeeandkitchen.fitnessjournal.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.catscoffeeandkitchen.fitnessjournal.R
import com.catscoffeeandkitchen.fitnessjournal.TestTags
import com.catscoffeeandkitchen.fitnessjournal.services.TimerService
import com.catscoffeeandkitchen.ui.groups.ExerciseGroupScreen
import com.catscoffeeandkitchen.ui.search.SearchExercisesScreen
import com.catscoffeeandkitchen.home.ui.HomeScreen
import com.catscoffeeandkitchen.stats.ui.StatsScreen
import com.catscoffeeandkitchen.ui.SettingsScreen
import com.catscoffeeandkitchen.ui.SelectPlanScreen
import com.catscoffeeandkitchen.ui.detail.WorkoutDetailsScreen
import com.catscoffeeandkitchen.ui.detail.WorkoutPlanDetailScreen
import com.catscoffeeandkitchen.ui.groups.detail.GroupDetailScreen
import com.catscoffeeandkitchen.ui.list.WorkoutPlansScreen
import com.catscoffeeandkitchen.ui.list.WorkoutsScreen
import com.catscoffeeandkitchen.ui.navigation.FitnessJournalBottomNavigationBar
import com.catscoffeeandkitchen.ui.navigation.FitnessJournalTopAppBar
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen
import com.catscoffeeandkitchen.ui.search.SearchExercisesMultiSelectScreen

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun Navigation(
    timerService: TimerService?,
    onStartTimer: (Long, Long) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        startDestination = LiftingLogScreen.HomeScreen.route,
        enterTransition = { fadeIn(initialAlpha = .3f, animationSpec = tween(easing = FastOutSlowInEasing)) },
        exitTransition = { fadeOut(animationSpec = tween(easing = FastOutSlowInEasing)) },
    ) {
        composable(
            LiftingLogScreen.HomeScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "liftinglog://app/${LiftingLogScreen.HomeScreen.route}"
                }
            ),
        ) {
            Scaffold(
                topBar = { 
                    FitnessJournalTopAppBar(
                        title = stringResource(id = R.string.app_name),
                        showAppIcon = true,
                        onNavigateToSettings = {
                            navController.navigate(LiftingLogScreen.Settings.route)
                        }
                    )
                },
                bottomBar = {
                    FitnessJournalBottomNavigationBar(navController = navController)
                },
            ) { padding ->
                HomeScreen(
                    navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            LiftingLogScreen.WorkoutsScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "liftinglog://app/${LiftingLogScreen.WorkoutsScreen.route}"
                }
            ),
        ) {
            Scaffold(
                topBar = {
                    FitnessJournalTopAppBar(
                        stringResource(id = R.string.workouts),
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
                            navController.navigate(
                                LiftingLogScreen.NewWorkoutScreen.route
                            )
                        },
                        modifier = Modifier.testTag(TestTags.FAB)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "create workout")
                    }
                }
            )  { padding ->
                WorkoutsScreen(
                    navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            LiftingLogScreen.WorkoutPlansScreen.route
        ) {
            WorkoutPlansScreen(navController)
        }

        composable(
            LiftingLogScreen.NewWorkoutScreen.route,
            arguments = listOf(
                navArgument("planId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            Scaffold { padding ->
                SelectPlanScreen(
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            "${LiftingLogScreen.WorkoutDetails.route}/{workoutId}?plan={plan}",
            deepLinks = listOf(navDeepLink {
                uriPattern = "liftinglog://app/${LiftingLogScreen.WorkoutDetails.route}/{workoutId}"
            }),
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType },
                navArgument("plan") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            )
        ) {
            Scaffold { padding ->
                WorkoutDetailsScreen(
                    navController,
                    timerService = null,
                    onStartTimer = onStartTimer,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            "${LiftingLogScreen.WorkoutPlanEditScreen.route}/{workoutId}",
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType },
            )
        ) {
            Scaffold { padding ->
                WorkoutPlanDetailScreen(
                    navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            LiftingLogScreen.StatsScreen.route
        ) {
            Scaffold(
                topBar = {
                    FitnessJournalTopAppBar(
                        title = stringResource(id = R.string.stats),
                        onNavigateToSettings = {
                            navController.navigate(LiftingLogScreen.Settings.route)
                        }
                    )
                },
                bottomBar = {
                    FitnessJournalBottomNavigationBar(navController = navController)
                }
            )  { padding ->
                StatsScreen(modifier = Modifier
                    .padding(padding)
                )
            }
        }

        composable(
            "${LiftingLogScreen.SearchExercisesScreen.route}?" +
                    "category={category}&muscle={muscle}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("muscle") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            )
        ) { entry ->
            val muscle = entry.arguments?.getString("muscle")
            val category = entry.arguments?.getString("category")

            Scaffold { padding ->
                SearchExercisesScreen(
                    navController,
                    muscle = muscle,
                    category = category,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            "${LiftingLogScreen.SearchExercisesMultiSelectScreen.route}?" +
                    "category={category}&muscle={muscle}&selectedExercises={selectedExercises}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("muscle") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("selectedExercises") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            val muscle = entry.arguments?.getString("muscle")
            val category = entry.arguments?.getString("category")

            SearchExercisesMultiSelectScreen(
                navController,
                muscle = muscle,
                category = category,
            )
        }

        composable(
            "${LiftingLogScreen.ExerciseGroupListScreen.route}?selectable={selectable}",
            arguments = listOf(
                navArgument("selectable") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val selectable = backStackEntry.arguments?.getBoolean("selectable")

            Scaffold(
                topBar = {
                    FitnessJournalTopAppBar(
                        title = stringResource(id = R.string.exercise_groups),
                        onNavigateToSettings = null
                    )
                },
                floatingActionButtonPosition = FabPosition.End,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(
                                LiftingLogScreen.SearchExercisesMultiSelectScreen.route
                            )
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "create group")
                    }
                }
            ) { padding ->
                ExerciseGroupScreen(
                    navController,
                    selectable = selectable ?: false,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            LiftingLogScreen.ExerciseGroupDetailScreen().route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val addingExercise by backStackEntry.savedStateHandle
                .getStateFlow<String?>("exerciseToAdd", null)
                .collectAsState()

            GroupDetailScreen(
                addingExercise = addingExercise,
                navigateToAddExercise = {
                    navController.navigate(
                        LiftingLogScreen.SearchExercisesScreen.route
                    )
                }
            )
        }

        composable(
            LiftingLogScreen.Settings.route
        )  {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text(stringResource(id = R.string.settings)) })
                }
            ) { padding ->
                SettingsScreen(modifier = Modifier.padding(padding))
            }
        }
    }
}
