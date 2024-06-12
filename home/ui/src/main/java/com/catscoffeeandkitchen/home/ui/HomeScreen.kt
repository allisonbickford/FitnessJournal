package com.catscoffeeandkitchen.home.ui

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val unit by viewModel.weightUnit.collectAsState(initial = WeightUnit.Pounds)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = Spacing.Default),
        verticalArrangement = Arrangement.spacedBy(Spacing.Default)
    ) {
        item {
            HomeCard {
                NextWorkoutCardContent(
                    uiState.nextPlan
                ) { planId ->
                    navController.navigate(
                        LiftingLogScreen.NewWorkoutScreen.route +
                                "?plan=${planId}"
                    )
                }
            }
        }

        item {
            HomeCard {
                LastExercisesCardContent(
                    state = uiState.lastExercises
                )
            }
        }

        item {
            HomeCard {
                AverageWeekCardContent(
                    state = uiState.weekStats
                )
            }
        }

        item {
            HomeCard(
                modifier = Modifier
            ) {
                MostImprovedExerciseCardContent(
                    state = uiState.mostImprovedExercise,
                    unit = unit
                )
            }
        }
    }
}

@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    content: @Composable() ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Default),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.Default)
        ) {
            content()
        }
    }
}
