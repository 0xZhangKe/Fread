package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.search.StatusProviderSearchResult
import com.zhangke.utopia.status.search.StatusProviderSearchType
import javax.inject.Inject

internal class ActivityPubAccountSearchAdapter @Inject constructor() {

    fun adapt(webFinger: WebFinger, account: ActivityPubAccount): StatusProviderSearchResult {
        return StatusProviderSearchResult(
            type = StatusProviderSearchType.STATUS_SOURCE_OWNER,
            uri = webFinger.toString(),
            name = account.displayName,
            desc = account.note,
            thumbnail = account.avatar,
        )
    }
}
