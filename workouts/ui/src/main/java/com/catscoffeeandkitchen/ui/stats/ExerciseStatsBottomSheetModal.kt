package com.catscoffeeandkitchen.ui.stats

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.catscoffeeandkitchen.models.ExerciseEntries
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseStatsBottomSheetModal(
    exerciseId: Long,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val viewModel = hiltViewModel<ExerciseStatsViewModel, ExerciseStatsViewModel.Factory>(
        key = exerciseId.toString(),
        creationCallback = { factory: ExerciseStatsViewModel.Factory ->
            factory.create(exerciseId)
        }
    )

    val state by viewModel.uiState.collectAsState()
    val unit by viewModel.weightUnit.collectAsState(initial = WeightUnit.Pounds)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Text(
            state.exercise?.name.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        content()

        state.error?.let {
            Text(it.localizedMessage ?: "An unexpected error occurred.")
        }

        state.exercise?.let { exercise ->
            ExerciseStatsContent(
                exerciseEntries = ExerciseEntries(exercise, state.entries),
                unit = unit
            )
        }
    }
}
