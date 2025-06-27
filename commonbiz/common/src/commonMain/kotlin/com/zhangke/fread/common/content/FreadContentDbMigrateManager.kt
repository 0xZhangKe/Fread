package com.zhangke.fread.common.content

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.fread.common.db.ContentConfigDatabases
import com.zhangke.fread.common.status.adapter.ContentConfigAdapter
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.FreadContent
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class FreadContentDbMigrateManager @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val statusProvider: StatusProvider,
    private val contentConfigDatabases: ContentConfigDatabases,
    private val contentConfigAdapter: ContentConfigAdapter,
) {

    internal fun migrateOldDb() {
        ApplicationScope.launch {
            val contentConfigList = contentConfigDatabases.getContentConfigDao()
                .queryAllContentConfig()
            if (contentConfigList.isEmpty()) return@launch
            contentConfigList
                .map(contentConfigAdapter::toContentConfig)
                .mapNotNull { it.toContent() }
                .let { contentRepo.insertAll(it) }
            contentConfigDatabases.getContentConfigDao().deleteTable()
        }
    }

    private fun ContentConfig.toContent(): FreadContent? {
        if (this is ContentConfig.MixedContent) {
            return com.zhangke.fread.status.content.MixedContent(
                id = this.id.toString(),
                order = this.order,
                name = this.name,
                sourceUriList = this.sourceUriList,
            )
        }
        return statusProvider.contentManager.restoreContent(this)
    }
}
