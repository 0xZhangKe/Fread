package com.zhangke.utopia.feeds.adapter

import com.zhangke.utopia.feeds.model.Feeds
import com.zhangke.utopia.feeds.repo.db.FeedsEntity
import javax.inject.Inject

internal class FeedsEntityAdapter @Inject constructor() {

    fun adapt(entity: FeedsEntity): Feeds {
        return Feeds(
            name = entity.name,
            sourceList = entity.uriList
        )
    }
}
