package com.catscoffeeandkitchen.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.catscoffeeandkitchen.exercises.ui.R
import com.catscoffeeandkitchen.models.MuscleCategory

@Composable
@ReadOnlyComposable
fun muscleCategoryStrings(muscleCategory: MuscleCategory): String {
    return when (muscleCategory) {
        MuscleCategory.Chest -> stringResource(R.string.chest)
        MuscleCategory.Abs -> stringResource(R.string.abs)
        MuscleCategory.Legs -> stringResource(R.string.legs)
        MuscleCategory.Arms -> stringResource(R.string.arms)
        MuscleCategory.Back -> stringResource(R.string.back)
    }
}

@Composable
@ReadOnlyComposable
fun muscleStrings(muscle: String): String {
    return when (muscle) {
        "Chest" -> stringResource(R.string.chest)
        "Abs" -> stringResource(R.string.abs)
        "Hamstrings" -> stringResource(R.string.hamstrings)
        "Calves" -> stringResource(R.string.calves)
        "Glutes" -> stringResource(R.string.glutes)
        "Quads" -> stringResource(R.string.quads)
        "Biceps" -> stringResource(R.string.biceps)
        "Triceps" -> stringResource(R.string.triceps)
        "Lats" -> stringResource(R.string.lats)
        else -> muscle
    }
}