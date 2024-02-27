package com.catscoffeeandkitchen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.DayOfWeek

@Composable
fun WeekRow(
    weekdays: List<DayOfWeek>,
    onWeekdaySelected: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        DayOfWeek.entries.forEach { day ->
            FilterChip(
                selected = weekdays.contains(day),
                onClick = {
                    onWeekdaySelected(day)
                },
                label = { Text(
                    when (day) {
                        DayOfWeek.SUNDAY -> "Su"
                        DayOfWeek.THURSDAY -> "Th"
                        else -> day.name.first().toString()
                    }
                ) },
                leadingIcon = { },
            )
        }
    }
}
