package com.zhangke.fread.feeds.pages.manager.importing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.utils.PlatformUri

@Composable
actual fun OpenDocumentContainer(
    onResult: (PlatformUri) -> Unit,
    content: @Composable OpenDocumentContainerScope.() -> Unit,
) {
    val scope = remember {
        OpenDocumentContainerScope()
    }
    with(scope) {
        content()
    }
}

actual class OpenDocumentContainerScope {
    actual fun launch() {
        TODO("Not yet implemented")
    }
}