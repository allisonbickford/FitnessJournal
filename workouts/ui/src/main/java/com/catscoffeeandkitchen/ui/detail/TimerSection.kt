package com.catscoffeeandkitchen.ui.detail

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.catscoffeeandkitchen.ui.R
import com.catscoffeeandkitchen.ui.detail.exercise.TimeSinceText
import kotlinx.coroutines.delay
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun TimerSection(
    timers: List<Long>,
    secondsLeft: Long?,
    selectedTimer: Long,
    autoStartTimer: Boolean,
    onUpdateSelectedTimer: (Long) -> Unit,
    onToggleAutoStartTimer: () -> Unit,
    onStartTimer: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    var showNotificationRationaleDialog by remember { mutableStateOf(false) }

    val iconTint = animateColorAsState(targetValue = if (autoStartTimer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onStartTimer(selectedTimer)
    }

    if (showNotificationRationaleDialog) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(.7f),
            onDismissRequest = { showNotificationRationaleDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNotificationRationaleDialog = false
                        permissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
                    }
                ) { Text("OK") }
            },
            text = {
                Text(stringResource(R.string.notification_permission_explanation))
            }
        )
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            .padding(8.dp)
    ) {
        Text(stringResource(R.string.start_a_timer), style = MaterialTheme.typography.titleSmall)

        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            timers.forEach { amount ->
                TimerButton(amount) {
                    onUpdateSelectedTimer(amount)

                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            "android.permission.POST_NOTIFICATIONS"
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            onStartTimer(amount)
                        }
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            "android.permission.POST_NOTIFICATIONS"
                        ) -> {
                            showNotificationRationaleDialog = true
                        }
                        else -> {
                            permissionLauncher
                                .launch("android.permission.POST_NOTIFICATIONS")
                        }
                    }
                }
            }

            IconButton(
                onClick = onToggleAutoStartTimer
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_autoplay),
                    contentDescription = "automatically start timer when finishing a set",
                    modifier = Modifier,
                    tint = iconTint.value
                )
            }
        }

        if (secondsLeft != null) {
            TimeSinceText(
                secondsLeft = secondsLeft,
                totalTime = selectedTimer
            )
        }
    }
}


@Composable
fun TimerButton(
    seconds: Long,
    onClick: () -> Unit
) {
    TextButton(
        onClick = {
            onClick()
        },
    ) {
        val duration = seconds.toDuration(DurationUnit.SECONDS)
        Text(
            when {
                duration.inWholeSeconds < 60 -> duration.toString(DurationUnit.SECONDS)
                else -> duration.toString(DurationUnit.MINUTES, decimals = 1).replace(".0", "")
            }
        )
    }
}
