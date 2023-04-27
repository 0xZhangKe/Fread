package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.status.search.StatusProviderSearchResult
import com.zhangke.utopia.status.search.StatusProviderSearchType
import javax.inject.Inject

class InstanceSearchAdapter @Inject constructor() {

    fun adapt(instance: ActivityPubInstance): StatusProviderSearchResult {
        return StatusProviderSearchResult(
            type = StatusProviderSearchType.STATUS_SOURCE_OWNER,
            uri = instance.domain,
            name = instance.title,
            desc = instance.description,
            thumbnail = instance.thumbnail.url,
        )
    }
}
