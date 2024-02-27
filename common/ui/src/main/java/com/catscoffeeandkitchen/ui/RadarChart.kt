package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

data class BezierPoint(
    val destination: Offset,
    val bezier: Offset? = null
)

data class RadarChartPoint(
    val labelIndex: Int,
    val value: Int
)

data class RadarChartData(
    val labels: List<String>,
    val levels: Int = 5,
    val data: List<RadarChartPoint>
)


@Composable
fun RadarChart(
    chartData: RadarChartData,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.outline,
    fillColor: Color = MaterialTheme.colorScheme.primary,
    labelStyle: TextStyle = MaterialTheme.typography.titleMedium
) {
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val canvasSize = min(constraints.maxWidth, constraints.maxHeight)
        val canvasSizeDp = with(LocalDensity.current) { canvasSize.toDp() }

        val chartPadding = canvasSize * .25f
        val chartSize = Size(
            canvasSize.toFloat() - chartPadding,
            canvasSize.toFloat() - chartPadding
        )
        val arc = 360f / chartData.labels.size
        val radius = chartSize.width / 2f
        val segmentLength = radius / chartData.levels

        val dataPath = mutableListOf<BezierPoint>()
        Canvas(modifier = Modifier.size(canvasSizeDp)) {
            (0 until chartData.labels.size).forEach { index ->
                val angle = (arc * index) * PI / 180

                chartData.data.filter { it.labelIndex == index }.forEach { point ->
                    val dest = getCoordinates(
                        distance = (segmentLength * point.value),
                        angle = angle.toFloat(),
                        origin = center.x to center.y
                    )

                    val bezier = getCoordinates(
                        distance = (segmentLength * point.value),
                        angle = angle.toFloat() + ((35) * PI / 180).toFloat(),
                        origin = center.x to center.y
                    )

                    dataPath.add(
                        BezierPoint(
                            Offset(dest.first, dest.second),
                            Offset(bezier.first, bezier.second)
                        )
                    )
                }

                drawLine(
                    color = lineColor,
                    start = getCoordinates(
                        distance = segmentLength,
                        angle = angle.toFloat(),
                        origin = center.x to center.y
                    ).let {
                        Offset(it.first, it.second)
                    },
                    end = getCoordinates(
                        distance = radius,
                        angle = angle.toFloat(),
                        origin = center.x to center.y
                    ).let {
                        Offset(it.first, it.second)
                    },
                    strokeWidth = 3f
                )

                drawPoints(
                    points = (1..chartData.levels).map { value ->
                        getCoordinates(
                            distance = (segmentLength * value),
                            angle = angle.toFloat(),
                            origin = center.x to center.y
                        ).let {
                            Offset(it.first, it.second)
                        }
                    },
                    pointMode = PointMode.Points,
                    cap = StrokeCap.Round,
                    strokeWidth = 10f,
                    color = lineColor
                )

                val measuredLabel = textMeasurer.measure(chartData.labels[index])

                drawText(
                    textMeasurer = textMeasurer,
                    text = chartData.labels[index],
                    style = labelStyle,
                    topLeft = getCoordinates(
                        distance = radius + (measuredLabel.size.height / 1.5f),
                        angle = angle.toFloat(),
                        origin = center.x to center.y
                    ).let { Offset(
                        it.first - measuredLabel.size.center.x,
                        it.second - measuredLabel.size.center.y
                    ) }
                )
            }

            (1..chartData.levels).forEach { value ->
                drawPath(
                    path = Path().apply {
                        val start = getCoordinates(
                            distance = (segmentLength * value) + 2,
                            angle = (arc * PI / 180).toFloat(),
                            origin = center.x to center.y
                        )
                        moveTo(start.first, start.second)

                        (0..chartData.labels.size).map { line ->
                            val web = getCoordinates(
                                distance = (segmentLength * value) + 1,
                                angle = (arc * line * PI / 180)
                                    .toFloat(),
                                origin = center.x to center.y
                            )

                            lineTo(web.first, web.second)
                        }

                        close()
                    },
                    color = lineColor,
                    alpha = 0.6f,
                    style = Stroke(
                        width = 2f
                    )
                )
            }

            drawPath(
                path = Path().apply {
                    if (dataPath.size == 1) {
                        moveTo(center.x, center.y)
                    } else {
                        dataPath.firstOrNull()?.let {
                            moveTo(it.destination.x, it.destination.y)
                        }
                    }

                    dataPath.forEach { point ->
                        lineTo(point.destination.x, point.destination.y)
                    }

                    close()
                },
                color = fillColor,
                alpha = 0.6f
            )

            drawPath(
                path = Path().apply {
                    if (dataPath.size == 1) {
                        moveTo(center.x, center.y)
                    } else {
                        dataPath.firstOrNull()?.let {
                            moveTo(it.destination.x, it.destination.y)
                        }
                    }

                    dataPath.forEach { point ->
                        lineTo(point.destination.x, point.destination.y)
                    }

                    close()
                },
                color = fillColor,
                style = Stroke(
                    width = 12f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

private fun getCoordinates(
    distance: Float,
    angle: Float,
    origin: Pair<Float, Float>
): Pair<Float, Float> {
    return origin.first + (distance * cos(angle)) to
            origin.second + (distance * sin(angle))
}


@Preview(
    heightDp = 1200
)
@Composable
fun RadarChartPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadarChart(
                chartData = RadarChartData(
                    labels = listOf("first", "second", "third"),
                    data = emptyList()
                )
            )

            RadarChart(
                chartData = RadarChartData(
                    labels = (0 until 6).toList().map { it.toString() },
                    data = (0..5).map {
                        RadarChartPoint(Random.nextInt(6), Random.nextInt(9))
                    },
                    levels = 8
                )
            )

            RadarChart(
                chartData = RadarChartData(
                    labels = (0..10).toList().map { it.toString() },
                    data = emptyList()
                )
            )
        }
    }
}
