package com.catscoffeeandkitchen.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WorkoutNameAndNoteSection(
    workoutName: String,
    workoutNote: String?,
    onUpdateName: (String) -> Unit = {},
    onUpdateNote: (String?) -> Unit = {},
) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(workoutName) }
    var note by remember { mutableStateOf(workoutNote) }

    Column(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (isEditing) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text("workout name") },
                )

                IconButton(
                    onClick = {
                        isEditing = false
                        if (name != workoutName) {
                            onUpdateName(name)
                        }

                        if (note != workoutNote) {
                            onUpdateNote(note)
                        }
                    },
                ) {
                    Icon(Icons.Default.Check, "stop editing name and note")
                }
            }

            OutlinedTextField(
                value = note.orEmpty(),
                onValueChange = { newNote ->
                    note = newNote.ifEmpty { null }
                },
                singleLine = true,
                label = { Text("note") },
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    name,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = { isEditing = true },
                ) {
                    Icon(Icons.Default.Edit, "edit name and note")
                }
            }

            if (!note.isNullOrEmpty()) {
                Text(
                    note ?: "",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
