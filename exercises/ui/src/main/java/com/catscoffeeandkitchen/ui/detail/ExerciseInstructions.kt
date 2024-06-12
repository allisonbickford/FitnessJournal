package com.catscoffeeandkitchen.ui.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun ExerciseInstructions(
    instruction: String
) {
    val listItems = instruction
        .replace("</li>", "")
        .split("<li>")
        .map { it.replace(Regex("<[^>]*>"), "") }

    Text(
        listItems.joinToString("\n"),
        modifier = Modifier
            .padding(
                vertical = Spacing.Half,
                horizontal = Spacing.Default
            )
    )
}