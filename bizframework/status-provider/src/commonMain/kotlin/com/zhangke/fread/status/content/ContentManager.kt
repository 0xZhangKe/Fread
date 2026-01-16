package com.zhangke.fread.status.content

import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.TextString
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.platform.BlogPlatform

class ContentManager(
    private val providerList: List<IContentManager>
) {

    suspend fun addContent(
        platform: BlogPlatform,
        action: AddContentAction,
    ) {
        providerList.forEach { it.addContent(platform, action) }
    }

    fun restoreContent(config: ContentConfig): FreadContent? {
        return providerList.firstNotNullOfOrNull { it.restoreContent(config) }
    }
}

interface IContentManager {

    suspend fun addContent(platform: BlogPlatform, action: AddContentAction)

    fun restoreContent(config: ContentConfig): FreadContent?
}

data class AddContentAction(
    val onShowSnackBarMessage: suspend (TextString) -> Unit,
    val onFinishPage: suspend () -> Unit,
    val onOpenNewPage: suspend (NavKey) -> Unit,
)
