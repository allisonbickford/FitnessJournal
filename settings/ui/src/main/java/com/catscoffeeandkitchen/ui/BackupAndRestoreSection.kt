package com.catscoffeeandkitchen.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catscoffeeandkitchen.settings.ui.R
import java.io.File

@Composable
fun BackupAndRestoreSection(
    showAppClosingDialog: Boolean,
    backupData: (uri: Uri?) -> Unit,
    restoreData: (file: File?) -> Unit,
    closeApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFileNameDialog by remember { mutableStateOf(false) }

    AppClosingDialog(isVisible = showAppClosingDialog, closeApp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            .padding(12.dp)
    ) {
        Text(
            stringResource(id = R.string.local_data),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            stringResource(R.string.restoring_data_description)
        )

        Text(
            stringResource(R.string.restoring_data_warning),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(12.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )

        BackupButton(
            backupData = backupData,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            closeFileNameDialog = {
                showFileNameDialog = false
            }
        )

        RestoreButton(
            restoreData = restoreData,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
fun AppClosingDialog(
    isVisible: Boolean,
    closeApp: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    closeApp()
                }) { Text("OK") }
            },
            dismissButton = {},
            title = { Text(stringResource(R.string.app_closing)) },
            text = { Text(stringResource(R.string.app_closing_description))}
        )
    }
}

