package com.catscoffeeandkitchen.ui.detail.exercise.inputs

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme


@Composable
fun ExerciseSetButtonInput(
    value: String,
    label: String,
    labelColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    enabled: Boolean = true,
    horizontalBias: Float = -1f
) {
    val alignment by animateHorizontalAlignmentAsState(horizontalBias)

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(backgroundColor),
        ) {
            Text(
                value,
                color = valueColor,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(alignment),
            color = labelColor
        )
    }
}

@Composable
private fun animateHorizontalAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment.Horizontal> {
    val bias by animateFloatAsState(targetBiasValue)
    return remember {
        derivedStateOf { BiasAlignment.Horizontal(bias) }
    }
}

@Preview(
    showBackground = false,
)
@Composable
fun SetButtonPreview() {
    LiftingLogTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            for (i in 0 until 4) {
                ExerciseSetButtonInput(
                    value = (i + 5).toString(),
                    label = "reps",
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    onClick = { }
                )
            }
        }
    }
}
