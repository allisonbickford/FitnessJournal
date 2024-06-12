package com.catscoffeeandkitchen.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Suppress("MagicNumber")
fun LazyGridScope.MonthItems(
    months: Int,
    dates: List<OffsetDateTime>
) {
    for (monthsAgo in 0 until months) {
        val dateMonthsAgo = OffsetDateTime.now()
            .withDayOfMonth(1)
            .minusMonths(monthsAgo.toLong())

        val firstSunday = dateMonthsAgo
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        val daysWithBlanks = Duration.between(
            firstSunday,
            dateMonthsAgo
                .with(TemporalAdjusters.lastDayOfMonth())
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        ).toDays().toInt()

        MonthHeader(dateMonthsAgo)

        items(daysWithBlanks) { number ->
            val day = firstSunday.plusDays(number.toLong())

            val matchingDate = dates.find { completedDate ->
                day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ==
                        completedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }

            if (day.month != dateMonthsAgo.month) {
                Text("")
            } else {
                Text(
                    day.dayOfMonth.toString(),
                    textAlign = TextAlign.Center,
                    color = when {
                        day.toLocalDate() == LocalDate.now() -> MaterialTheme.colorScheme.primary
                        matchingDate != null -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onBackground
                    },
                    modifier = Modifier
                        .padding(6.dp)
                        .border(
                            width = 1.dp,
                            color = if (day.toLocalDate() == LocalDate.now())
                                MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            shape = MaterialTheme.shapes.small
                        )
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            if (matchingDate != null)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.background
                        )
                        .padding(6.dp)
                )
            }
        }

        item(
            span = { GridItemSpan(7) }
        ) {
            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
    }
}

@Suppress("MagicNumber")
fun LazyGridScope.MonthHeader(
    dateMonthsAgo: OffsetDateTime
) {
    item(
        span = { GridItemSpan(7) }
    ) {
        Text(
            "${dateMonthsAgo.month.name.lowercase()} ${dateMonthsAgo.year}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(12.dp)
        )
    }

    items(7) { number ->
        val day = dateMonthsAgo.withDayOfMonth(1)
            .with(DayOfWeek.SUNDAY)
            .plusDays(number.toLong())

        Text(
            day.dayOfWeek.name.lowercase().take(3),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(12.dp)
        )
    }
}
