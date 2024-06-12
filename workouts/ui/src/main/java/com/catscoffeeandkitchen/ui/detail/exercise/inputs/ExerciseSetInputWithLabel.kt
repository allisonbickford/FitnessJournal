package com.catscoffeeandkitchen.ui.detail.exercise.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp


@Composable
fun ExerciseSetInputWithLabel(
    value: String,
    label: String,
    labelColor: Color,
    enabled: Boolean,
    updateValue: (value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = FocusRequester()

    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    var focusTextKey by remember { mutableStateOf("")  }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }


    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        BasicTextField(
            value = textFieldValue,
            singleLine = true,
            onValueChange = { newText ->
                textFieldValue = newText
            },
            readOnly = !enabled,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            modifier = modifier
                .requiredWidth(60.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { state ->
                    if (state.hasFocus) {
                        textFieldValue = textFieldValue.copy(
                            selection = TextRange(0, textFieldValue.text.length)
                        )
                        focusTextKey = value
                    } else {
                        focusTextKey = ""
                        if (textFieldValue.text != value) {
                            updateValue(textFieldValue.text)
                        }
                    }
                },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = {
                updateValue(textFieldValue.text)
            }),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                ) {
                    innerTextField()
                }
            }
        )

        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = labelColor
        )
    }
}

