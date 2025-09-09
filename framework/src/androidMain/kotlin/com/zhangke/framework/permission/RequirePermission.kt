package com.zhangke.framework.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.utils.extractActivity
import com.zhangke.fread.localization.Res
import com.zhangke.fread.localization.alert
import com.zhangke.fread.localization.permission_write_external_permission_denied
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequirePermission(
    permissionString: String,
    onPermissionGranted: suspend () -> Unit,
    onPermissionDenied: suspend (() -> Unit) = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val permissionState = rememberPermissionState(
        permission = permissionString,
        onPermissionResult = {
            if (!it) {
                coroutineScope.launch {
                    onPermissionDenied()
                }
            }
        }
    )
    val permissionStatus = permissionState.status
    if (permissionStatus == PermissionStatus.Granted) {
        LaunchedEffect(permissionState) {
            onPermissionGranted()
        }
    } else {
        if (permissionStatus.shouldShowRationale) {
            LaunchedEffect(permissionState) {
                permissionState.launchPermissionRequest()
            }
        } else {
            var showDeniedDialog by remember {
                mutableStateOf(true)
            }
            if (showDeniedDialog) {
                FreadDialog(
                    title = stringResource(Res.string.alert),
                    onDismissRequest = {
                        showDeniedDialog = false
                    },
                    contentText = stringResource(Res.string.permission_write_external_permission_denied),
                    onNegativeClick = {
                        showDeniedDialog = false
                        coroutineScope.launch {
                            onPermissionDenied()
                        }
                    },
                    onPositiveClick = {
                        coroutineScope.launch {
                            onPermissionDenied()
                        }
                        openSettingActivity(context)
                    },
                )
            }
        }
    }
}

private fun openSettingActivity(context: Context) {
    val activity = context.extractActivity() ?: return
    val localIntent = Intent()
    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
    localIntent.data = Uri.fromParts("package", activity.packageName, null)
    activity.startActivity(localIntent)
}
