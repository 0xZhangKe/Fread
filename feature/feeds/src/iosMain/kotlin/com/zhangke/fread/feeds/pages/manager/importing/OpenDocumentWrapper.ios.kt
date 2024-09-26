package com.zhangke.fread.feeds.pages.manager.importing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.utils.PlatformUri

@Composable
actual fun OpenDocumentWrapper(
    onResult: (PlatformUri) -> Unit,
    content: @Composable OpenDocumentWrapperScope.() -> Unit,
) {
    val scope = remember {
        OpenDocumentWrapperScope()
    }
    with(scope) {
        content()
    }
}

actual class OpenDocumentWrapperScope {
    actual fun launch() {
        TODO("Not yet implemented")
    }
}