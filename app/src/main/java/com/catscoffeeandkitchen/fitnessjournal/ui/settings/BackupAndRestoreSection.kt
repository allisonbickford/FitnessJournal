package com.catscoffeeandkitchen.fitnessjournal.ui.settings

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.catscoffeeandkitchen.domain.util.DataState
import com.catscoffeeandkitchen.fitnessjournal.ui.components.FitnessJournalButton
import timber.log.Timber
import java.io.File
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupAndRestoreSection(
    showAppClosingDialog: Boolean,
    lastBackupDate: OffsetDateTime,
    backupData: (file: File?) -> Unit,
    restoreData: (file: File?) -> Unit,
    closeApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showFileNameDialog by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showFileNameDialog = true
        } else {
            backupData(null)
        }
    }

    val chooseFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val file = createTempFile(
                        directory = context.cacheDir,
                        prefix = "backup",
                        suffix = ".fjbackup"
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.copy(inputStream, file.outputStream())
                    }
                    inputStream.close()
                    if (file.extension == "fjbackup") {
                        restoreData(file)
                    } else {
                        Toast.makeText(context, "Not a backup file.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                Toast.makeText(context, "There was a problem getting that file.", Toast.LENGTH_SHORT).show()
                Timber.e(ex)
            }
        }
    }

    val requestReadPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            chooseFileLauncher.launch("*/*")
        } else {
            restoreData(null)
        }
    }

    if (showFileNameDialog) {
        FileNameDialog(
            onCancel = { showFileNameDialog = false },
            backupData = { f ->
                showFileNameDialog = false
                backupData(f) },
        )
    }

    if (showAppClosingDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    closeApp()
                }) { Text("OK") }
            },
            dismissButton = {},
            title = { Text("App Closing") },
            text = { Text("The app will now close to refresh the database.")}
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Restoring data will close the app so it can be completely restarted" +
                    " with the correct data.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(12.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )

        FitnessJournalButton(
            "Backup Data",
            fullWidth = true,
            onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                    ) -> {
                        showFileNameDialog = true
                    }
                    else -> {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        requestPermissionLauncher.launch(
                            "android.permission.WRITE_EXTERNAL_STORAGE"
                        )
                    }
                }
            }
        )

        FitnessJournalButton(
            "Restore Data",
            fullWidth = true,
            onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        "android.permission.READ_EXTERNAL_STORAGE"
                    ) -> {
                        chooseFileLauncher.launch("*/*")
                    }
                    else -> {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        requestReadPermissionLauncher.launch(
                            "android.permission.READ_EXTERNAL_STORAGE"
                        )
                    }
                }

            }
        )

        Text(
            "last backup: ${lastBackupDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))}",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

