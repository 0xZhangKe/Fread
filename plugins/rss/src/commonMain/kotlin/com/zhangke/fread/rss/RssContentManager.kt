package com.zhangke.fread.rss

import com.zhangke.fread.status.content.AddContentAction
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class RssContentManager @Inject constructor() : IContentManager {

    override suspend fun addContent(platform: BlogPlatform, action: AddContentAction) {

    }

    override fun restoreContent(config: ContentConfig): FreadContent? {
        return null
    }
}
