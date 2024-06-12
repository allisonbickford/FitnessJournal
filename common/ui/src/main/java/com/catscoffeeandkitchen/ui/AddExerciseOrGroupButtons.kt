package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.common.ui.R
import com.catscoffeeandkitchen.ui.components.LLButton
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun AddExerciseOrGroupButtons(
    addExercise: () -> Unit,
    addGroup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.Half),
        modifier = modifier
    ) {
        LLButton(
            onClick = {
                addExercise()
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(painterResource(id = R.drawable.fitness_center), "group")
            Text(
                "Add Exercise",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        LLButton(
            onClick = {
                addGroup()
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(painterResource(id = R.drawable.dataset), "group")
            Text(
                "Add Group",
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
