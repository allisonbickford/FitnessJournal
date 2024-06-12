package com.catscoffeeandkitchen.ui.detail.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.models.ExerciseSet
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.ui.R
import timber.log.Timber


@Composable
fun NextPRText(
    nextSet: ExerciseSet,
    bestSet: ExerciseSet?,
    unit: WeightUnit,
    color: Color = Color.Unspecified
) {
    if (!nextSet.isComplete && bestSet != null) {
        val repsToHitPR = (1..3).toList().firstOrNull { add ->
            nextSet.copy(reps = nextSet.reps + add).repMax(unit) > bestSet.repMax(unit)
        }

        Timber.d("reps to hit PR = $repsToHitPR")
        Timber.d("weight fraction to hit PR = ${(nextSet.weightInPounds / bestSet.weightInPounds) * 100}")

        if (repsToHitPR != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.add_reps_to_hit_pr,
                        count = repsToHitPR,
                        repsToHitPR
                    ),
                    textAlign = TextAlign.End,
                    color = color
                )
            }
        } else if (nextSet.copy(weightInPounds = nextSet.weightInPounds * 1.2f).repMaxInPounds > bestSet.repMaxInPounds) {
            var addedWeight = 2.5f
            while (nextSet.copy(weightInPounds = nextSet.weightInPounds + addedWeight)
                    .repMaxInPounds < bestSet.repMaxInPounds) {
                addedWeight += 2.5f
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = when (unit) {
                        WeightUnit.Pounds ->
                            stringResource(id = R.string.add_lbs_to_hit_pr, addedWeight)
                        WeightUnit.Kilograms ->
                            stringResource(id = R.string.add_kg_to_hit_pr, addedWeight)
                    },
                    color = color,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
