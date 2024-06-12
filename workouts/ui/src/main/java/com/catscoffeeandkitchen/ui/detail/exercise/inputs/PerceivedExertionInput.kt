package com.catscoffeeandkitchen.ui.detail.exercise.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.ui.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerceivedExertionInput(
    pe: Int,
    onUpdate: (Int) -> Unit,
    onUseKeyboard: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onUseKeyboard) {
            Icon(
                painterResource(id = R.drawable.ic_keyboard),
                "use keyboard"
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (6..10).forEach { exertion ->
                FilterChip(
                    pe == exertion,
                    label = { Text("$exertion") },
                    onClick = { onUpdate(exertion) }
                )
            }
        }
    }
}
