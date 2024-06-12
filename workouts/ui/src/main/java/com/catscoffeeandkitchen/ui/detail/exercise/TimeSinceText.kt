package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.math.absoluteValue

@Composable
fun TimeSinceText(
    secondsLeft: Long,
    totalTime: Long,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = secondsLeft > -3
    ) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            val displayedSeconds = secondsLeft.coerceAtLeast(0L)
            Text(
                "${displayedSeconds / 60}:${if (displayedSeconds % 60 < 10) "0" else ""}${displayedSeconds % 60}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            LinearProgressIndicator(
                progress = { 1 - ((totalTime - displayedSeconds).toFloat() / totalTime.toFloat()) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
