package com.zhangke.fread.feeds.pages.manager.importing

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toPlatformUri

@Composable
actual fun OpenDocumentContainer(
    onResult: (PlatformUri) -> Unit,
    content: @Composable OpenDocumentContainerScope.() -> Unit,
) {
    val selectedFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.toPlatformUri()?.let { onResult(it) }
        }
    val scope = remember {
        OpenDocumentContainerScope(selectedFileLauncher)
    }
    with(scope) {
        content()
    }
}

actual class OpenDocumentContainerScope(
    private val launcher: ManagedActivityResultLauncher<Array<String>, Uri?>
) {
    actual fun launch() {
        launcher.launch(arrayOf("*/*"))
    }
}