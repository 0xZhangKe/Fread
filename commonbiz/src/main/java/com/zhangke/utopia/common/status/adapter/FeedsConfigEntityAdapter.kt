package com.zhangke.utopia.common.status.adapter

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.db.FeedsConfigEntity
import javax.inject.Inject

class FeedsConfigEntityAdapter @Inject constructor() {

    fun toFeedsConfig(entity: FeedsConfigEntity) = FeedsConfig(
        id = entity.id,
        name = entity.name,
        sourceUriList = entity.sourceUriList,
    )

    fun toEntity(config: FeedsConfig) = FeedsConfigEntity(
        id = config.id,
        name = config.name,
        sourceUriList = config.sourceUriList,
    )
}
