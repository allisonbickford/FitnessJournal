package com.catscoffeeandkitchen.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.catscoffeeandkitchen.common.ui.R

sealed class LiftingLogScreen(
    val route: String,
    @StringRes
    val resourceId: Int,
    @DrawableRes
    val iconId: Int = R.drawable.fitness_center,
    val icon: ImageVector? = null,
    val testTag: String = "NavItem"
) {
    open fun routeWithArgs(vararg args: String): String {
        return route
    }

    data object Settings : LiftingLogScreen(
        route = "settings",
        resourceId = R.string.settings,
        icon = Icons.Default.Settings,
        testTag = "SettingsNavItem"
    )

    data object HomeScreen : LiftingLogScreen(
        route = "home",
        resourceId = R.string.home,
        icon = Icons.Default.Home,
        testTag = "HomeNavItem"
    )

    data object WorkoutsScreen : LiftingLogScreen(
        route = "workouts",
        resourceId = R.string.workouts,
        testTag = "WorkoutsNavItem"
    )
    data object WorkoutPlansScreen : LiftingLogScreen(
        route = "plans",
        resourceId = R.string.workout_plans,
        iconId = R.drawable.checklist,
        testTag = "WorkoutPlansNavItem"
    )
    data object WorkoutDetails : LiftingLogScreen(
        route = "workouts",
        resourceId = R.string.workout_details,
        testTag = "WorkoutDetailsNavItem"
    )
    data object NewWorkoutScreen : LiftingLogScreen(
        route = "workouts/new?planId={planId}",
        resourceId =  R.string.new_workout,
        testTag = "NewWorkoutNavItem"
    )
    data object StatsScreen : LiftingLogScreen(
        route = "stats",
        resourceId = R.string.stats,
        iconId = R.drawable.bar_chart,
        testTag = "StatsNavItem"
    )
    data object SearchExercisesScreen : LiftingLogScreen(
        route = "exercises",
        resourceId = R.string.search_exercises,
        testTag = "SearchExercisesNavItem"
    )
    data object ExerciseDetailScreen : LiftingLogScreen(
        route = "exercises/{exerciseId}",
        resourceId = R.string.search_exercises,
        testTag = "ExerciseDetailNavItem"
    ) {
        override fun routeWithArgs(vararg args: String): String {
            require(args.size == 1)

            return route
                .replace("{exerciseId}", args.first())
        }
    }
    data object SearchExercisesMultiSelectScreen : LiftingLogScreen(
        route = "exercises/multiselect",
        resourceId = R.string.search_exercises_multi,
        testTag = "SelectMultipleExercisesNavItem"
    )
    data object WorkoutPlanEditScreen : LiftingLogScreen(
        route = "plans",
        resourceId = R.string.workout_plan,
        testTag = "WorkoutPlanEditNavItem"
    )
    data object ExerciseGroupListScreen : LiftingLogScreen(
        route = "exercises/groups",
        resourceId = R.string.exercise_groups,
        iconId = R.drawable.dataset,
        testTag = "ExerciseGroupNavItem"
    )

    class ExerciseGroupDetailScreen : LiftingLogScreen(
        route = "exercises/groups/{groupId}",
        resourceId = R.string.exercise_group,
        testTag = "ExerciseGroupDetailNavItem"
    ) {
        override fun routeWithArgs(vararg args: String): String {
            require(args.size == 1)

            return route.replace("{groupId}", args.first())
        }
    }
}