package com.catscoffeeandkitchen.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.extension.orZero
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun ExerciseStatsChart(
    stats: ExerciseProgressStats,
    unit: WeightUnit
) {
    val earliest = stats.earliestSetCompletedAt ?: OffsetDateTime.now()

    val xToSets = stats.sets.map { set ->
        stats.chronoUnit.between(
            earliest,
            set.completedAt ?: OffsetDateTime.now()
        ).toFloat() to set
    }
    val entryPairs = xToSets.associate { (x, y) ->
        x to y.repMax(unit).toFloat()
    }
    val xToSetMap = xToSets.associate { (x, y) -> x to y }

    val chartEntryModel = entryModelOf(entryPairs.map { (x, y) -> FloatEntry(x, y) })

    val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")
    val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom>
    { value, _ ->
        when (stats.chronoUnit) {
            ChronoUnit.DAYS -> earliest.plus(value.toLong(), stats.chronoUnit)
                .format(dateTimeFormatter)
            ChronoUnit.WEEKS -> earliest.plus(value.toLong(), stats.chronoUnit)
                .format(dateTimeFormatter)
            else -> earliest.plus(value.toLong(), stats.chronoUnit)
                .format(DateTimeFormatter.ofPattern("MMM"))
        }
    }

    Chart(
        chart = lineChart(
            lines = listOf(LineChart.LineSpec(
                lineColor = MaterialTheme.colorScheme.primary.toArgb(),
                lineBackgroundShader = DynamicShaders.fromBrush(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                            Color.Transparent
                        )
                    )
                ),
                pointConnector = DefaultPointConnector(
                    cubicStrength = .25f
                )
            )),
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minY = (stats.worstSet?.weight(unit).orZero.roundToInt() - 5f)
                    .coerceAtLeast(0f),
                maxY = stats.bestSet?.weight(unit).orZero.roundToInt() + 5f,
            )
        ),
        model = chartEntryModel,
        startAxis = rememberStartAxis(
            itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 5)
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = horizontalAxisValueFormatter,
            sizeConstraint = Axis.SizeConstraint.TextWidth("MMM dd"),
            label = textComponent {
                lineCount = 2
                textSizeSp = MaterialTheme.typography.labelSmall.fontSize.value
                padding = dimensionsOf(vertical = 4.dp, horizontal = 2.dp)
            }
        ),
        marker = repMaxMarker(
            setData = xToSetMap,
            unit = unit
        ),
        getXStep = { _ -> 1f },
        chartScrollSpec = rememberChartScrollSpec(
            isScrollEnabled = false
        )
    )
}

@Preview
@Composable
fun ExerciseStatsChartPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            ExerciseStatsChart(
                stats = ExerciseProgressStats(
                    Exercise(name = "Bicep Curl"),
                    sets = listOf(
                        ExerciseSet(
                            id = 1,
                            setNumber = 1,
                            reps = 6,
                            weightInPounds = 6f,
                            completedAt = OffsetDateTime.now().minusMonths(2)
                        ),
                        ExerciseSet(
                            id = 2,
                            setNumber = 2,
                            reps = 7,
                            weightInPounds = 8f,
                            completedAt = OffsetDateTime.now().minusMonths(1)
                        ),
                        ExerciseSet(
                            id = 3,
                            reps = 7,
                            weightInPounds = 12f,
                            completedAt = OffsetDateTime.now().minusWeeks(1)
                        ),
                        ExerciseSet(
                            id = 4,
                            reps = 6,
                            weightInPounds = 15f,
                            completedAt = OffsetDateTime.now().minusDays(3)
                        )
                    )
                ),
                unit = WeightUnit.Pounds
            )
        }
    }
}
