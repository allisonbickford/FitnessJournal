package com.catscoffeeandkitchen.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.workoutplans.ui.R

@Composable
fun GroupExercisesDialog(
    exercises: List<String>,
    onDismiss: () -> Unit,
    createGroup: (name: String?) -> Unit
) {
    var groupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            groupName = ""
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    createGroup(groupName)
                    groupName = ""
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    groupName = ""
                    createGroup(null)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.skip))
            }
        },
        title = {
            Text(
                stringResource(R.string.name_this_group),
                style = MaterialTheme.typography.titleSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .height(((30 * exercises.size) + 120).dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    stringResource(
                        R.string.add_a_name_for_this_group_of_exercises_x,
                        exercises.joinToString("\n")
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                )
            }
        }
    )
}
