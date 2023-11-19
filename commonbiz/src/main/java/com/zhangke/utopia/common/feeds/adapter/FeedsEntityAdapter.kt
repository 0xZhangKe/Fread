package com.zhangke.utopia.common.feeds.adapter

import com.zhangke.utopia.common.feeds.model.Feeds
import com.zhangke.utopia.common.feeds.repo.FeedsEntity
import javax.inject.Inject

class FeedsEntityAdapter @Inject constructor() {

    fun adapt(entity: FeedsEntity): Feeds {
        return Feeds(
            id = entity.id,
            name = entity.name,
            sourceUriList = entity.uriList
        )
    }
}
