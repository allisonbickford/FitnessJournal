package com.catscoffeeandkitchen.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.catscoffeeandkitchen.common.ui.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessJournalTopAppBar(
    title: String,
    onNavigateToSettings: (() -> Unit)?,
    showAppIcon: Boolean = false
) {
    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showAppIcon) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lifting_log),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(title)
            }
        },
        actions = {
            onNavigateToSettings?.let { navigate ->
                IconButton(
                    onClick = navigate,
                    modifier = Modifier.testTag(LiftingLogScreen.Settings.testTag)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "go to settings"
                    )
                }
            }
        }
    )
}

@Composable
fun FitnessJournalBottomNavigationBar(
    navController: NavController
) {
    NavigationBar {
        NavigationBarItem(
            selected = navController.currentDestination?.route?.contains("home") == true,
            onClick = { navController.navigate(LiftingLogScreen.HomeScreen.route) },
            icon = { BottomBarIcon(screen = LiftingLogScreen.HomeScreen) },
            label = { Text("Home") },
            modifier = Modifier.testTag(LiftingLogScreen.HomeScreen.testTag)
        )

        NavigationBarItem(
            selected = navController.currentDestination?.route?.contains("workout") == true,
            onClick = { navController.navigate(LiftingLogScreen.WorkoutsScreen.route) },
            icon = { BottomBarIcon(screen = LiftingLogScreen.WorkoutsScreen) },
            label = { Text("Workouts") },
            modifier = Modifier.testTag(LiftingLogScreen.WorkoutsScreen.testTag)
        )

        NavigationBarItem(
            selected = navController.currentDestination?.route?.contains("plans") == true,
            onClick = { navController.navigate(LiftingLogScreen.WorkoutPlansScreen.route) },
            icon = { BottomBarIcon(screen = LiftingLogScreen.WorkoutPlansScreen) },
            label = { Text("Plans") },
            modifier = Modifier.testTag(LiftingLogScreen.WorkoutPlansScreen.testTag)
        )

        NavigationBarItem(
            selected = navController.currentDestination?.route?.contains("stats") == true,
            onClick = { navController.navigate(LiftingLogScreen.StatsScreen.route) },
            icon = { BottomBarIcon(screen = LiftingLogScreen.StatsScreen) },
            label = { Text("Stats") },
            modifier = Modifier.testTag(LiftingLogScreen.StatsScreen.testTag)
        )
    }
}

@Composable
fun BottomBarIcon(screen: LiftingLogScreen) {
    if (screen.icon != null) {
        Icon(screen.icon!!, stringResource(id = screen.resourceId))
    } else {
        Icon(painterResource(id = screen.iconId), stringResource(id = screen.resourceId))
    }
}

