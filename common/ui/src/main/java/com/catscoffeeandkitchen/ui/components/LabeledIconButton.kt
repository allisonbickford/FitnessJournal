package com.catscoffeeandkitchen.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.common.ui.R
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme


@Composable
fun LabeledIconButton(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) Color.Unspecified
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            icon()
        }
    }
}

@Preview
@Composable
private fun LabeledIconButtonPreview() {
    LiftingLogTheme {
        Card {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                LabeledIconButton(
                    label = "Action",
                    icon = { Icon(Icons.Default.Add, "perform action") },
                    onClick = {  }
                )

                LabeledIconButton(
                    label = "Disabled",
                    icon = {
                        Icon(
                            painterResource(R.drawable.fitness_center),
                            "perform action"
                        )
                    },
                    enabled = false,
                    onClick = {  }
                )
            }
        }
    }
}
