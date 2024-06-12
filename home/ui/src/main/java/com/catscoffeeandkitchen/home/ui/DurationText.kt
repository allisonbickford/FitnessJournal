package com.catscoffeeandkitchen.home.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun DurationText(
    chronoUnit: ChronoUnit,
    from: OffsetDateTime,
    to: OffsetDateTime = OffsetDateTime.now(),
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        when (chronoUnit) {
            ChronoUnit.MINUTES -> stringResource(
                R.string.over_x_minutes,
                chronoUnit.between(from, to)
            )
            ChronoUnit.HOURS -> stringResource(
                R.string.over_x_hours,
                chronoUnit.between(from, to)
            )
            ChronoUnit.DAYS -> stringResource(
                R.string.over_x_days,
                chronoUnit.between(from, to)
            )
            ChronoUnit.MONTHS -> stringResource(
                R.string.over_x_months,
                chronoUnit.between(from, to)
            )
            ChronoUnit.YEARS -> stringResource(
                R.string.over_x_years,
                chronoUnit.between(from, to)
            )
            else -> stringResource(
                R.string.since_date,
                from.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            )
        },
        style = style
    )
}