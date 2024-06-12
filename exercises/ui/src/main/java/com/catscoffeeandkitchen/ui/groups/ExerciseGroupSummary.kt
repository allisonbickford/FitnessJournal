package com.catscoffeeandkitchen.ui.groups

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.exercises.ui.R
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.ui.theme.Spacing
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ExerciseGroupSummary(
    group: ExerciseGroup,
    shouldAnimate: Boolean,
    renameGroup: (String) -> Unit,
    editExercises: () -> Unit,
    removeGroup: () -> Unit,
    onAnimationFinished: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    var showExtrasDropdown by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf(group.name) }

    val highlightColor by animateColorAsState(
        targetValue = if (shouldAnimate) MaterialTheme.colorScheme.surfaceContainerHighest
                        else MaterialTheme.colorScheme.surface,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(durationMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        finishedListener = {
            onAnimationFinished()
        }
    )

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        renameGroup(groupName.orEmpty())
                        showRenameDialog = false
                    },
                    enabled = !groupName.isNullOrBlank()
                ) {
                    Text("OK")
                }
            },
            dismissButton = { },
            title = {
                Text(
                    stringResource(R.string.rename_group),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            text = {
                Column(modifier = Modifier
                    .fillMaxWidth(.8f)
                    .height(50.dp)) {
                    TextField(groupName.orEmpty(), onValueChange = { groupName = it })
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .background(color = highlightColor)
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = { if (onClick != null) onClick() }),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Default)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    group.name.orEmpty().ifEmpty { stringResource(id = R.string.exercise_group) },
                    style = MaterialTheme.typography.titleLarge
                )

                Box(modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = { showExtrasDropdown = !showExtrasDropdown },
                    ) {
                        Icon(Icons.Default.MoreVert, "more group options")
                    }
                }

                DropdownMenu(
                    expanded = showExtrasDropdown,
                    onDismissRequest = { showExtrasDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("rename") },
                        leadingIcon = { Icon(Icons.Default.Edit, "rename group") },
                        onClick = {
                            showExtrasDropdown = false
                            showRenameDialog = true
                        })

                    DropdownMenuItem(
                        text = { Text("edit exercises") },
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                "change exercises in group"
                            )
                        },
                        onClick = {
                            showExtrasDropdown = false
                            editExercises()
                        })

                    DropdownMenuItem(
                        text = { Text("remove") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                "delete group"
                            )
                        },
                        onClick = {
                            showExtrasDropdown = false
                            removeGroup()
                        })
                }
            }

            group.exercises.forEach { exercise ->
                Column(
                    modifier = Modifier.padding(start = Spacing.Default),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Half)
                ) {
                    Text(
                        exercise.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    exercise.stats?.lastCompletedAt?.let { lastCompleted ->
                        Text(
                            "last completed ${lastCompleted.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }

                    exercise.stats?.amountCompleted?.let { amount ->
                        Text(
                            if (amount > 0) "completed $amount times" else "never completed",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }

            }
        }
    }
}
