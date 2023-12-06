package com.zhangke.utopia.common.feeds.adapter

import com.zhangke.utopia.common.feeds.model.FeedsConfig
import com.zhangke.utopia.common.feeds.repo.config.FeedsConfigEntity
import javax.inject.Inject

class FeedsConfigEntityAdapter @Inject constructor() {

    fun toFeedsConfig(entity: FeedsConfigEntity) = FeedsConfig(
        id = entity.id,
        authorUserId = entity.authorUserId,
        name = entity.name,
        sourceUriList = entity.sourceUriList,
        databaseFilePath = entity.databaseFilePath,
    )

    fun toEntity(config: FeedsConfig) = FeedsConfigEntity(
        id = config.id,
        authorUserId = config.authorUserId,
        name = config.name,
        sourceUriList = config.sourceUriList,
        databaseFilePath = config.databaseFilePath,
    )
}
