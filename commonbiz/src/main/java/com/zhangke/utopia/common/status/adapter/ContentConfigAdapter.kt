package com.zhangke.utopia.common.status.adapter

import com.zhangke.utopia.common.status.repo.db.ContentConfigEntity
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType
import javax.inject.Inject

class ContentConfigAdapter @Inject constructor() {

    fun toContentConfig(entity: ContentConfigEntity): ContentConfig {
        return when (entity.type) {
            ContentType.MIXED -> ContentConfig.MixedContent(
                id = entity.id,
                name = entity.name,
                sourceUriList = entity.sourceUriList!!,
                lastReadStatusId = entity.lastReadStatusId,
            )

            ContentType.ACTIVITY_PUB -> ContentConfig.ActivityPubContent(
                id = entity.id,
                name = entity.name,
                baseUrl = entity.baseUrl!!,
            )
        }
    }

    fun toEntity(config: ContentConfig): ContentConfigEntity {
        return when (config) {
            is ContentConfig.MixedContent -> ContentConfigEntity(
                id = config.id,
                name = config.name,
                type = ContentType.MIXED,
                sourceUriList = config.sourceUriList,
                lastReadStatusId = config.lastReadStatusId,
                baseUrl = null,
            )

            is ContentConfig.ActivityPubContent -> ContentConfigEntity(
                id = config.id,
                name = config.name,
                type = ContentType.ACTIVITY_PUB,
                sourceUriList = null,
                lastReadStatusId = null,
                baseUrl = config.baseUrl,
            )
        }
    }
}
