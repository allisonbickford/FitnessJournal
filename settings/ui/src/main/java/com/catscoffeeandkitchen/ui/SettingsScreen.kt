package com.catscoffeeandkitchen.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.catscoffeeandkitchen.models.WeightUnit
import com.catscoffeeandkitchen.settings.ui.R
import kotlin.system.exitProcess


@Suppress("LongMethod")
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val weightUnit by viewModel.weightUnit.collectAsState(initial = WeightUnit.Pounds)
    val timers by viewModel.timers.collectAsState(initial = emptyList())

    val restoreStatus by viewModel.restoreStatus.collectAsState(null)
    val importStatus by viewModel.importStatus.collectAsState(null)
    val exportStatus by viewModel.exportStatus.collectAsState(null)
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        item {
            UnitsSection(
                weightUnit,
                onUpdate = { unit ->
                    viewModel.setWeightUnit(unit)
                }
            )
        }

        item {
            TimerSection(
                timers,
                onUpdateTimer = { timer, seconds ->
                    viewModel.setTimer(timer, seconds)
                }
            )
        }

        item {
            CsvSection(
                importStatus = importStatus,
                importFromCsv = { uri ->
                    viewModel.importFromCSV(uri)
                },
                exportStatus = exportStatus,
                exportToCsv = { uri ->
                    viewModel.exportToCsv(uri)
                }
            )
        }

        item {
            BackupAndRestoreSection(
                showAppClosingDialog = restoreStatus != null,
                backupData = { uri ->
                    if (uri != null) {
                        viewModel.backupDataToExternalFile(uri)
                    } else {
                        viewModel.backupData()
                    }},
                restoreData = { file ->
                    if (file != null) {
                        viewModel.restoreDataFromFile(file)
                    } else {
                        viewModel.restoreData()
                    }
                },
                closeApp = {
                    val packageManager: PackageManager = context.packageManager

                    val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                    val componentName: ComponentName = intent.component!!
                    val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)

                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    if (context is Activity) {
                        context.finishAndRemoveTask()
                    }
                    exitProcess(0)
                }
            )
        }

        item {
            val uriHandler = LocalUriHandler.current
            val privacyPolicyUrl = stringResource(id = R.string.privacy_policy_url)

            TextButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = {
                    uriHandler.openUri(privacyPolicyUrl)
                }
            ) {
                Text("View Privacy Policy")
            }
        }
    }
}
