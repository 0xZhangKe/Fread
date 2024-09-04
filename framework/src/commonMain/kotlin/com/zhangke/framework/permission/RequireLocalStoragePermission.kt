package com.zhangke.framework.permission

import androidx.compose.runtime.Composable

@Composable
expect fun RequireLocalStoragePermission(
    onPermissionGranted: suspend () -> Unit,
    onPermissionDenied: suspend (() -> Unit) = {},
)