package com.zhangke.fread.bluesky.internal.content

import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreen
import com.zhangke.fread.status.content.AddContentAction
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class BlueskyContentManager @Inject constructor() : IContentManager {

    override suspend fun addContent(
        platform: BlogPlatform,
        action: AddContentAction
    ) {
        if (platform.protocol.notBluesky) return
        action.onFinishPage()
        action.onOpenNewPage(AddBlueskyContentScreen(platform.baseUrl))
    }

    override fun restoreContent(config: ContentConfig): FreadContent? {
        // Bluesky does not container any old content data
        return null
    }
}
