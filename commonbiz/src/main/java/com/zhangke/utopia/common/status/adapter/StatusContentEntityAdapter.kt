package com.zhangke.utopia.common.status.adapter

import com.zhangke.utopia.common.status.StatusIdGenerator
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusType
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class StatusContentEntityAdapter @Inject constructor(
    private val statusIdGenerator: StatusIdGenerator,
) {

    fun toStatus(entity: StatusContentEntity): Status {
        return entity.status
    }

    fun toEntityList(
        sourceUri: FormalUri,
        statusList: List<Status>,
        nextIdOfLatest: String? = null,
    ): List<StatusContentEntity> {
        return statusList.mapIndexed { index, status ->
            val nextStatusId = if (index == statusList.lastIndex) {
                nextIdOfLatest
            } else {
                statusList[index + 1].id
            }
            toEntity(sourceUri = sourceUri, status = status, nextStatusId = nextStatusId)
        }
    }

    fun toEntity(
        sourceUri: FormalUri,
        status: Status,
        nextStatusId: String?,
    ): StatusContentEntity {
        return StatusContentEntity(
            id = statusIdGenerator.generate(sourceUri, status),
            nextStatusId = nextStatusId,
            sourceUri = sourceUri,
            type = StatusType.BLOG,
            statusIdOfPlatform = status.id,
            status = status,
            createTimestamp = status.datetime,
        )
    }
}
