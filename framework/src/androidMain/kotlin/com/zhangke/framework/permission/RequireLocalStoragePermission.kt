package com.zhangke.framework.permission

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun RequireLocalStoragePermission(
    onPermissionGranted: suspend () -> Unit,
    onPermissionDenied: suspend (() -> Unit) = {},
) {
    val context = LocalContext.current
    if (context.hasWriteStoragePermission()) {
        LaunchedEffect(Unit) {
            onPermissionGranted()
        }
    } else {
        RequirePermission(
            permissionString = Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onPermissionGranted = onPermissionGranted,
            onPermissionDenied = onPermissionDenied,
        )
    }
}
