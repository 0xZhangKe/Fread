package com.zhangke.fread.feeds.pages.manager.importing

import androidx.compose.runtime.Composable
import com.zhangke.framework.utils.PlatformUri

@Composable
expect fun OpenDocumentContainer(
    onResult: (PlatformUri) -> Unit,
    content: @Composable OpenDocumentContainerScope.() -> Unit
)

expect class OpenDocumentContainerScope {
    fun launch()
}