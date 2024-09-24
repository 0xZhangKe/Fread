package com.zhangke.framework.permission

import androidx.compose.runtime.Composable

@Composable
actual fun RequireLocalStoragePermission(
    onPermissionGranted: suspend () -> Unit,
    onPermissionDenied: suspend () -> Unit
) {
    TODO("Not yet implemented")
}
