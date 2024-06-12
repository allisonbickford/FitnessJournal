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
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt

@Composable
fun CsvImportButton(
    importStatus: Double?,
    importFromCsv: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf(true) }
    var showImportErrorDialog by remember { mutableStateOf(true) }

    val chooseCsvLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            importFromCsv(uri)
        }
    }

    val requestReadPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            chooseCsvLauncher.launch("*/*")
        } else {
            Toast.makeText(context, "Cannot import CSV without file access permissions.", Toast.LENGTH_SHORT).show()
        }
    }

    if (importStatus != null && importStatus > 0.0) {
        ImportProgressAlertDialog(count = importStatus.roundToInt())
    } else if (showImportDialog && importStatus != null && importStatus <= 0) {
        ImportCompleteAlertDialog {
            showImportDialog = false
        }
    } else if (showImportErrorDialog && importStatus == null) {
//        ErrorAlertDialog(error = importStatus.e) {
//            showImportErrorDialog = false
//        }
    }

    LLButton(
        modifier = modifier.fillMaxWidth(),
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
                    chooseCsvLauncher.launch("*/*")
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestReadPermissionLauncher.launch(
                        "android.permission.$permission"
                    )
                }
            }
        }
    ) {
        Text("Import from CSV")
    }
}

@Composable
fun ErrorAlertDialog(error: Throwable, closeDialog: () -> Unit) {
    val errorDescription = when (error) {
//        is CsvFormatError -> "The format of the CSV could not be read."
//        is DatabaseError -> "Could not merge CSV data with existing data."
        is DateTimeParseException -> "Could not read the dates from CSV file."
        else -> "Could not read CSV."
    }

    AlertDialog(
        onDismissRequest = { closeDialog() },
        confirmButton = {
            TextButton(onClick = { closeDialog() } ) {
                Text("OK")
            }
        },
        dismissButton = {},
        title = { Text("Import Error") },
        text = {
            Text(errorDescription)
        }
    )
}

@Composable
fun ImportCompleteAlertDialog(closeDialog: () -> Unit) {
    AlertDialog(
        onDismissRequest = { closeDialog() },
        confirmButton = {
            TextButton(onClick = { closeDialog() } ) {
                Text("OK")
            }
        },
        dismissButton = {},
        title = { Text("Importing Data") },
        text = {
            Text("Import completed!")
        }
    )
}

@Composable
fun ImportProgressAlertDialog(
    count: Int?,
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        title = { Text("Importing Data") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator()
                count?.let { importCount ->
                    Text("Imported $importCount workouts")
                }
            }
        }
    )
}
