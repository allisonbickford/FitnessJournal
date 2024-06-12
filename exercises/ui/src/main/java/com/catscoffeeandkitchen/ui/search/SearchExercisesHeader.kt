package com.catscoffeeandkitchen.ui.search

import android.graphics.BlurMaskFilter
import android.graphics.PathEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.exercises.ui.R
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import com.catscoffeeandkitchen.ui.theme.Spacing
import com.catscoffeeandkitchen.common.ui.R as commonR

@Composable
fun SearchExerciseHeader(
    currentSearch: String?,
    muscleFilter: String?,
    categoryFilter: String?,
    onSearch: (search: String?) -> Unit,
    filterMuscle: (muscle: String?) -> Unit,
    filterCategory: (category: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var search by remember { mutableStateOf(TextFieldValue(currentSearch.orEmpty())) }
    val categoryOptions = stringArrayResource(id = R.array.search_exercise_category)
    val muscleOptions = stringArrayResource(id = R.array.search_muscles)

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = Spacing.Half)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Half),
            value = search,
            onValueChange = { value ->
                search = value
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSearch(search.text.trim())
                    keyboardController?.hide()
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSearch(search.text.trim().ifEmpty { null })
                        keyboardController?.hide()
                    }
                ) {
                    Icon(Icons.Default.Search, "search")
                }
            }
        )

        Text(
            stringResource(commonR.string.muscles),
            modifier = Modifier.padding(start = Spacing.Half, top = Spacing.Half),
            style = MaterialTheme.typography.labelSmall
        )
        LazyRow(
            modifier = Modifier
                .padding(start = Spacing.Half),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(muscleOptions) { option ->
                FilterChip(
                    selected = muscleFilter == option,
                    onClick = {
                        val filter = if (muscleFilter == option) {
                            null
                        } else {
                            option
                        }
                        filterMuscle(filter)
                    },
                    label = { Text(option) }
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = Spacing.Half)
        )

        Text(
            stringResource(commonR.string.category),
            modifier = Modifier.padding(start = Spacing.Half, top = Spacing.Half),
            style = MaterialTheme.typography.labelSmall
        )
        LazyRow(
            modifier = Modifier
                .padding(start = Spacing.Half),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(categoryOptions) { option ->
                FilterChip(
                    selected = categoryFilter == option,
                    onClick = {
                        val filter = if (categoryFilter == option) {
                            null
                        } else {
                            option
                        }
                        filterCategory(filter)
                    },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchExerciseHeaderPreview() {
    LiftingLogTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            SearchExerciseHeader(
                "Bicep Curl",
                "Chest",
                "Pecs",
                { },
                { },
                { }
            )

            Text("Results")
        }
    }
}
