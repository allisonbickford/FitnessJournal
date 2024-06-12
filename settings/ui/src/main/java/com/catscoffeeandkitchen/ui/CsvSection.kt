package com.catscoffeeandkitchen.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.settings.ui.R


@Composable
fun CsvSection(
    importStatus: Double?,
    exportStatus: Int?,
    importFromCsv: (Uri) -> Unit,
    exportToCsv: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            .padding(12.dp)
    ) {
        Text(
            stringResource(R.string.workout_data),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            stringResource(R.string.workout_data_settings_description)
        )

        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp)
        ) {
            Text(
                stringResource(R.string.workout_data_disclaimer),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        CsvImportButton(
            importStatus,
            importFromCsv,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        CsvExportButton(
            exportStatus,
            exportToCsv,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
