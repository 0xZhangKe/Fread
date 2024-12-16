package com.zhangke.fread.common.status.adapter

import com.zhangke.fread.common.db.StatusContentEntity
import com.zhangke.fread.common.status.StatusIdGenerator
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class StatusContentEntityAdapter @Inject constructor(
    private val statusIdGenerator: StatusIdGenerator,
) {

    fun toStatus(entity: StatusContentEntity): Status {
        return entity.status
    }

    fun toEntityList(
        sourceUri: FormalUri,
        statusList: List<Status>,
        isFirstStatus: Boolean,
    ): List<StatusContentEntity> {
        return statusList.map {
            toEntity(sourceUri = sourceUri, status = it, isFirstStatus = isFirstStatus)
        }
    }

    fun toEntity(
        sourceUri: FormalUri,
        status: Status,
        isFirstStatus: Boolean,
    ): StatusContentEntity {
        return StatusContentEntity(
            id = statusIdGenerator.generate(sourceUri, status),
            isFirstStatus = isFirstStatus,
            sourceUri = sourceUri,
            statusIdOfPlatform = status.id,
            status = status,
            createTimestamp = status.datetime,
        )
    }
}
