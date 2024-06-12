package com.catscoffeeandkitchen.ui

import android.content.pm.PackageManager
import android.os.Build
import android.os.FileUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
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
import java.io.File
import kotlin.io.path.createTempFile
import kotlin.io.path.extension
import kotlin.io.path.outputStream

@Composable
fun RestoreButton(
    restoreData: (File?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val chooseFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            @Suppress("TooGenericExceptionCaught")
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val file = createTempFile(
                        "backup",
                        suffix = ".llbackup"
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.copy(inputStream, file.outputStream())
                    }
                    inputStream.close()
                    if (file.extension == "llbackup") {
                        restoreData(file.toFile())
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

    LLButton(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            val permission = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> "READ_MEDIA_IMAGES"
                else -> "READ_EXTERNAL_STORAGE"
            }

            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    "android.permission.$permission"
                ) -> {
                    chooseFileLauncher.launch("*/*")
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
        Text(stringResource(R.string.restore_data))
    }
}
