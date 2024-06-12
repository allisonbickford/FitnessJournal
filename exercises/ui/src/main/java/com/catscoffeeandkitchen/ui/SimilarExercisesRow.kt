package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.catscoffeeandkitchen.exercises.ui.R
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing

@Composable
fun SimilarExercisesRow(
    exercises: List<Exercise>,
    modifier: Modifier = Modifier,
    onClickExercise: ((Exercise) -> Unit)? = null,
) {
    val width = LocalConfiguration.current.screenWidthDp * .33f

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Default),
        contentPadding = PaddingValues(horizontal = Spacing.Default)
    ) {
        items(exercises) { exercise ->
            Column(
                modifier = Modifier
                    .width(width.dp)
                    .clickable(
                        enabled = onClickExercise != null,
                        onClick = {
                            onClickExercise?.invoke(exercise)
                        }
                    )
            ) {
                AsyncImage(
                    model = exercise.imageUrl,
                    contentDescription = "Depiction of ${exercise.name}",
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.White)
                        .size(width.dp),
                    contentScale = ContentScale.Fit,
                    fallback = painterResource(id = R.drawable.ic_fitness_center),
                    alignment = Alignment.Center,
                    placeholder = BrushPainter(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.outline.copy(alpha = .5f),
                                Color.Transparent,
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .25f)
                            )
                        )
                    )
                )

                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = Spacing.Quarter)
                )
            }
        }
    }
}

@Preview
@Composable
fun SimilarExercisesRowPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            SimilarExercisesRow(
                exercises = listOf(
                    Exercise(
                        name = "Bicep Curl"
                    ),
                    Exercise(
                        name = "Arnold Press"
                    ),
                    Exercise(
                        name = "Hammer Curl"
                    )
                )
            )
        }
    }
}
