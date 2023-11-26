package com.zhangke.framework.utils

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberPickVisualMediaLauncher(
    maxItems: Int,
    onResult: (List<Uri>) -> Unit,
): ManagedActivityResultLauncher<PickVisualMediaRequest, *>? {
    return when {
        maxItems < 1 -> null
        maxItems > 1 -> rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxItems),
            onResult = onResult,
        )

        else -> rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> uri?.let { onResult(listOf(it)) } },
        )
    }
}