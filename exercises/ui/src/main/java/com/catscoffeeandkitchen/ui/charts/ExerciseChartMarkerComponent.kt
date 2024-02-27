package com.catscoffeeandkitchen.ui.charts

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.overlayingComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.DashedShape
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.extension.appendCompat
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter


@Composable
fun repMaxMarker(
    setData: Map<Float, ExerciseSet>,
    unit: WeightUnit
): MarkerComponent {
    val labels = setData.map { (x, y) ->
        val weightLabel = when (unit) {
            WeightUnit.Kilograms -> "${y.weightInKilograms}kg"
            WeightUnit.Pounds -> "${y.weightInPounds}lb"
        }

        x to "$weightLabel\n${y.reps} reps\n" +
                "${"%.1f".format(y.repMax(unit)).replace(".0", "")}${unit.name}"
    }.associate { (x, y) -> x to y }

    val indicator = overlayingComponent(
        outer = shapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.primaryContainer),
        inner = overlayingComponent(
            outer = shapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.primaryContainer),
            inner = shapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.onPrimaryContainer),
            innerPaddingAll = 8.dp
        ),
        innerPaddingAll = 8.dp
    )

    val guideline = lineComponent(
        color = MaterialTheme.colorScheme.primary.copy(alpha = .6f),
        thickness = 2.dp,
        shape = DashedShape(
            Shapes.pillShape,
            8f,
            4f
        )
    )

    return remember(indicator, guideline) {
        MarkerComponent(
            label = TextComponent.Builder().apply {
                lineCount = 3
            }.build(),
            indicator = indicator,
            guideline = guideline
        ).apply {
            labelFormatter = MarkerLabelFormatter { markedEntries, _ ->
                val buffer = SpannableStringBuilder()
                markedEntries.forEach { model ->
                    buffer.appendCompat(
                        labels[model.entry.x].orEmpty(),
                        ForegroundColorSpan(model.color),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                buffer
            }
        }
    }
}
