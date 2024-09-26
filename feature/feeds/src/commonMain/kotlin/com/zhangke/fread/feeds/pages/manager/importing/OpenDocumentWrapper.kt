package com.zhangke.fread.feeds.pages.manager.importing

import androidx.compose.runtime.Composable
import com.zhangke.framework.utils.PlatformUri

@Composable
expect fun OpenDocumentWrapper(
    onResult: (PlatformUri) -> Unit,
    content: @Composable OpenDocumentWrapperScope.() -> Unit
)

expect class OpenDocumentWrapperScope {
    fun launch()
}