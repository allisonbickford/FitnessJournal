package com.catscoffeeandkitchen.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun Shimmer(
    containerColor: Color,
    shimmerColor: Color,
    modifier: Modifier = Modifier,
    angle: Float = 45f,
    gradientWidth: Float = 1f,
) {
    val shimmerTransition = rememberInfiniteTransition(
        label = "Shimmer Transition"
    )
    val progress by shimmerTransition.animateFloat(
        initialValue = 4f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Gradient Start"
    )

    Box(
        modifier.drawBehind {
            val angleRad = angle.coerceIn(0f, 360f) / 180f * PI
            val square = max(size.height, size.width) * gradientWidth

            val centerX = (1f - progress) * center.x
            val center = Offset(centerX, center.y)

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(containerColor, shimmerColor, containerColor),
                    start = center - Offset(
                        (square * cos(angleRad)).toFloat(),
                        (square * sin(angleRad)).toFloat()
                    ),
                    end = center + Offset(
                        (square * cos(angleRad)).toFloat(),
                        (square * sin(angleRad)).toFloat()
                    ),
                    tileMode = TileMode.Clamp
                ),
                size = size,
                alpha = .25f,
            )

            drawRect(
                color = containerColor,
                alpha = .25f,
                size = size
            )

//            drawPoints(
//                listOf(center),
//                color = Color.Black,
//                pointMode = PointMode.Points,
//                strokeWidth = 15f
//            )
        }
    )
}

@Preview
@Composable
private fun ShimmerPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Shimmer(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                shimmerColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .size(200.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ShimmerPreviewDark() {
    LiftingLogTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Shimmer(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                shimmerColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .size(200.dp)
            )
        }
    }
}
