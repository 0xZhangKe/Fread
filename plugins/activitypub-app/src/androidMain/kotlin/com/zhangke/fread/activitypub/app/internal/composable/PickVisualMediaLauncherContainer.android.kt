package com.zhangke.fread.activitypub.app.internal.composable

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.buildPickVisualImageRequest
import com.zhangke.framework.utils.buildPickVisualMediaRequest
import com.zhangke.framework.utils.toPlatformUri

@Composable
actual fun PickVisualMediaLauncherContainer(
    onResult: (List<PlatformUri>) -> Unit,
    maxItems: Int,
    content: @Composable PickVisualMediaLauncherContainerScope.() -> Unit,
) {
    val launcher = when {
        maxItems > 1 -> rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxItems),
            onResult = { uris ->
                onResult(uris.map { it.toPlatformUri() })
            },
        )

        else -> rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let { onResult(listOf(it.toPlatformUri())) }
            },
        )
    }
    val scope = remember(launcher) {
        PickVisualMediaLauncherContainerScope(launcher)
    }
    with(scope) {
        content()
    }
}

actual class PickVisualMediaLauncherContainerScope(
    private val launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, *>,
) {
    actual fun launchImage() {
        launcher.launch(buildPickVisualImageRequest())
    }

    actual fun launchMedia() {
        launcher.launch(buildPickVisualMediaRequest())
    }
}