package com.zhangke.utopia.common.status.adapter

import com.zhangke.utopia.common.status.repo.db.StatusSourceEntity
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class StatusSourceEntityAdapter @Inject constructor() {

    fun toSource(entity: StatusSourceEntity): StatusSource {
        return StatusSource(
            uri = entity.uri,
            name = entity.name,
            description = entity.description,
            thumbnail = entity.thumbnail,
        )
    }

    fun toEntity(source: StatusSource) = StatusSourceEntity(
        uri = source.uri,
        name = source.name,
        description = source.description,
        thumbnail = source.thumbnail,
    )
}
