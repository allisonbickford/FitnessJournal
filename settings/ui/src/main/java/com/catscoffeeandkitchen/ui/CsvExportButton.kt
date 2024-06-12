package com.catscoffeeandkitchen.ui

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.catscoffeeandkitchen.ui.components.LLButton


@Composable
fun CsvExportButton(
    exportStatus: Int?,
    exportToCsv: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(true) }
    var showExportErrorDialog by remember { mutableStateOf(true) }

    val chooseSaveLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            exportToCsv(uri)
        }
    }

    val requestWritePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            chooseSaveLocationLauncher.launch("text/csv")
        } else {
            Toast.makeText(context, "Cannot export as CSV without file write permissions.", Toast.LENGTH_SHORT).show()
        }
    }

    if (exportStatus != null && exportStatus > 0) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { },
            dismissButton = {},
            title = { Text("Exporting Data") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Exported $exportStatus sets")
                }
            }
        )
    } else if (showExportErrorDialog) {
        AlertDialog(
            onDismissRequest = { showExportErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showExportErrorDialog = false }) {
                    Text("OK")
                }
            },
            dismissButton = {},
            title = { Text("Export Error") },
            text = {
                Text("There was a problem exporting your workouts.")
            }
        )
    } else if (showExportDialog && exportStatus != null) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false} ) {
                    Text("OK")
                }
            },
            dismissButton = {},
            title = { Text("Exporting Data") },
            text = {
                Text("Export completed!")
            }
        )
    }

    LLButton(
        onClick = {
            val permission = when {
                Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> "READ_MEDIA_IMAGES"
                else -> "WRITE_EXTERNAL_STORAGE"
            }

            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    "android.permission.$permission"
                ) -> {
                    chooseSaveLocationLauncher.launch("lifting_log.csv")
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestWritePermissionLauncher.launch(
                        "android.permission.$permission"
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Text("Export to CSV")
    }

}
