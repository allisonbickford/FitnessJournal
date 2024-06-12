package com.catscoffeeandkitchen.ui.search.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.catscoffeeandkitchen.exercises.ui.R
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.ui.components.LLButton
import com.catscoffeeandkitchen.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateOrChangeExerciseDialog(
    isCreating: Boolean = true,
    currentExercise: Exercise,
    onDismiss: () -> Unit = {},
    onConfirm: (Exercise) -> Unit = {},
) {
    var name by remember { mutableStateOf(currentExercise.name) }
    var category by remember { mutableStateOf(currentExercise.category) }
    var muscles by remember { mutableStateOf(currentExercise.musclesWorked) }
    var equipment by remember { mutableStateOf(currentExercise.equipment) }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    )  {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("${if (isCreating) "Create" else "Update"} Exercise") },
                    navigationIcon = { IconButton(onClick = { onDismiss() }) {
                        Icon(Icons.Default.Close, "cancel creating exercise") }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Spacing.Default),
                verticalArrangement = Arrangement.spacedBy(Spacing.Default)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) }
                )

                Text(stringResource(R.string.category), style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Quarter)
                ) {
                    MuscleCategory.entries.forEach { cat ->
                        InputChip(
                            selected = cat == category,
                            onClick = { category = cat },
                            label = { Text(cat.name) },
                        )
                    }
                }

                Text(stringResource(R.string.muscles_worked), style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Quarter)
                ) {
                    MuscleCategory.entries.flatMap { it.muscles }.forEach { muscle ->
                        InputChip(
                            selected = muscles.contains(muscle),
                            onClick = {
                                muscles = if (muscles.contains(muscle)) {
                                    muscles.filterNot { it == muscle }
                                } else {
                                    muscles + listOf(muscle)
                                }
                            },
                            label = { Text(muscle) },
                        )
                    }
                }

                Text(stringResource(R.string.equipment), style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Quarter)
                ) {
                    EquipmentType.entries.forEach { type ->
                        InputChip(
                            selected = equipment.contains(type),
                            onClick = {
                                equipment = if (equipment.contains(type)) {
                                    equipment.filterNot { it == type }
                                } else {
                                    equipment.plus(type)
                                }
                            },
                            label = { Text(type.name) },
                            leadingIcon = {
                                if (equipment.contains(type)) {
                                    Icon(Icons.Default.Check, "${type.name} selected")
                                }
                            }
                        )
                    }
                }

                LLButton(
                    onClick = {
                        onConfirm(
                            Exercise(
                                name = name,
                                musclesWorked = muscles,
                                category = category,
                                equipment = equipment
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isCreating) "Create" else "Save")
                }
            }
        }
    }
}
