package com.catscoffeeandkitchen.stats.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val uiStats by viewModel.statsUIState.collectAsState()

    // 0 = calendar, 1 = bar chart
    var currentTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = currentTab,
        ) {
            stringArrayResource(id = R.array.stats_tabs).forEachIndexed { index, tab ->
                Tab(
                    selected = currentTab == index,
                    onClick = { currentTab = index },
                    text = { Text(text = tab) }
                )
            }
        }

        if (uiStats.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        uiStats.error?.let {
            Text(it.localizedMessage ?: "Unknown Error")
        }

        when (currentTab) {
            0 -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    MonthItems(months = 3, dates = uiStats.workoutDates.orEmpty())
                }
            }
            else -> {
                LazyColumn(
                    modifier = modifier
                        .padding(horizontal = 8.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiStats.selectedExercise?.let { exercise ->
                        exerciseStatsContent(
                            selectedExercise = exercise,
                            entries = uiStats.entries.orEmpty(),
                            unit = uiStats.unit
                        )
                    }

                    if (uiStats.selectedExercise == null && uiStats.exercises != null) {
                        items(uiStats.exercises.orEmpty()) { exercise ->
                            ListItem(
                                headlineContent = {
                                    Text(text = exercise.name)
                                },
                                supportingContent = {
                                    exercise.category?.let {
                                        Text(it.name)
                                    }
                                },
                                leadingContent = {
                                    exercise.imageUrl?.let { url ->
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "Image of ${exercise.name}"
                                        )
                                    }
                                },
                                modifier = Modifier.clickable {
                                    viewModel.selectExercise(exercise)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
