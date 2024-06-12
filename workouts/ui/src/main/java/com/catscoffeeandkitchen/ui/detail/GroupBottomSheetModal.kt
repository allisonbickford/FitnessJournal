package com.catscoffeeandkitchen.ui.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupBottomSheetModal(
    group: ExerciseGroup,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Text(group.name ?: stringResource(R.string.exercise_group))

        content()

        LazyColumn(
            contentPadding = PaddingValues(bottom = Spacing.Default)
        ) {
            items(group.exercises) { exercise ->
                ListItem(
                    leadingContent = {
                        AsyncImage(
                            model = exercise.imageUrl,
                            contentDescription = "Depiction of ${exercise.name}",
                            modifier = Modifier.size(100.dp)
                        )
                    },
                    headlineContent = { Text(exercise.name) },
                    supportingContent = { Text(exercise.musclesWorked.joinToString(", ")) }
                )
            }

        }
    }
}