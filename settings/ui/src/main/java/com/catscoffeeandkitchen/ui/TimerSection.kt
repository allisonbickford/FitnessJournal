package com.catscoffeeandkitchen.ui

import android.graphics.Outline
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.catscoffeeandkitchen.settings.ui.R

@Composable
fun TimerSection(
    timers: List<Long>,
    onUpdateTimer: (Int, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var minutesText by remember { mutableStateOf("") }

    var secondsText by remember { mutableStateOf("") }
    val secondsFocusRequester = FocusRequester()

    var editingTimer by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            .padding(12.dp),
    ) {
        Text(
            stringResource(R.string.timers),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            stringResource(R.string.timer_settings_description)
        )

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            timers.forEachIndexed { index, seconds ->
                val timerNumber = index + 1

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = modifier
                ) {
                    TextButton(
                        onClick = {
                            minutesText = seconds.div(60).toString()
                            secondsText = seconds.mod(60).toString()
                            editingTimer = timerNumber
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp)),
                    ) {
                        Text(
                            seconds.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Text(
                        "Timer $timerNumber",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }


    if (editingTimer > 0 && editingTimer <= timers.size) {
        Dialog(onDismissRequest = { editingTimer = 0 }) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Timer $editingTimer Duration",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Row {
                        OutlinedTextField(
                            value = minutesText,
                            onValueChange = { minutesText = it },
                            label = { Text("minutes")},
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    secondsFocusRequester.requestFocus()
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.padding(start = 12.dp))

                        OutlinedTextField(
                            value = secondsText,
                            onValueChange = { secondsText = it },
                            label = { Text("seconds")},
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    onUpdateTimer(
                                        editingTimer,
                                        (minutesText.ifEmpty { "0" }.toLong() * 60L)
                                                + secondsText.ifEmpty { "0" }.toLong()
                                    )
                                    editingTimer = 0
                                }
                            ),
                            modifier = Modifier.weight(1f).focusRequester(secondsFocusRequester)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = {
                            onUpdateTimer(
                                editingTimer,
                                (minutesText.ifEmpty { "0" }.toLong() * 60L)
                                        + secondsText.ifEmpty { "0" }.toLong()
                            )
                            editingTimer = 0
                        }) {
                            Text(stringResource(id = R.string.save))
                        }
                    }
                }
            }
        }
    }
}
