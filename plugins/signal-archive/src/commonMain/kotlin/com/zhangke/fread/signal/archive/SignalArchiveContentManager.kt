package com.zhangke.fread.signal.archive

import com.zhangke.fread.status.content.AddContentAction
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.SIGNAL_ARCHIVE_ID
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.signal.archive.screens.add.AddSignalArchiveContentNavKey

class SignalArchiveContentManager() : IContentManager {

    override suspend fun addContent(platform: BlogPlatform, action: AddContentAction) {
        if (platform.protocol.id != SIGNAL_ARCHIVE_ID) return
        action.onOpenNewPage(AddSignalArchiveContentNavKey())
    }

    override fun restoreContent(config: ContentConfig): FreadContent? {
        return null
    }
}
