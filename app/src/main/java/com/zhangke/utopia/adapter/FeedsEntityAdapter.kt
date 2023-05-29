package com.zhangke.utopia.adapter

import com.zhangke.utopia.db.FeedsEntity
import com.zhangke.utopia.domain.Feeds
import javax.inject.Inject

class FeedsEntityAdapter @Inject constructor() {

    fun adapt(entity: FeedsEntity): Feeds {
        return Feeds(
            name = entity.name,
            sourceList = entity.uriList
        )
    }
}
