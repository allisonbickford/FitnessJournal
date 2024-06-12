package com.catscoffeeandkitchen.ui

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.catscoffeeandkitchen.settings.ui.R
import com.catscoffeeandkitchen.ui.components.LLButton
import timber.log.Timber

@Composable
fun BackupButton(
    backupData: (Uri?) -> Unit,
    closeFileNameDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val chooseSaveLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri != null) {
            @Suppress("TooGenericExceptionCaught")
            try {
                closeFileNameDialog()
                backupData(uri)
                Toast.makeText(context, "Successfully backed up data.", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(context, "There was a problem getting that location.", Toast.LENGTH_SHORT).show()
                Timber.e(ex)
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            chooseSaveLocationLauncher.launch("lifting_log_backup")
        } else {
            backupData(null)
        }
    }


    LLButton(
        onClick = {
            val permission = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> "READ_MEDIA_IMAGES"
                else -> "WRITE_EXTERNAL_STORAGE"
            }

            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    "android.permission.$permission"
                ) -> {
                    chooseSaveLocationLauncher.launch("lifting_log_backup.llbackup")
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        "android.permission.$permission"
                    )
                }
            }
        },
        modifier = modifier
    ) {
        Text(stringResource(R.string.backup_data))
    }
}