package com.zhangke.fread.common.status.adapter

import com.zhangke.fread.common.db.ContentConfigEntity
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.ContentType

class ContentConfigAdapter () {

    fun toContentConfig(entity: ContentConfigEntity): ContentConfig {
        return when (entity.type) {
            ContentType.MIXED -> ContentConfig.MixedContent(
                id = entity.id,
                order = entity.order,
                name = entity.name,
                sourceUriList = entity.sourceUriList!!,
            )

            ContentType.ACTIVITY_PUB -> ContentConfig.ActivityPubContent(
                id = entity.id,
                order = entity.order,
                name = entity.name,
                baseUrl = entity.baseUrl!!,
                showingTabList = entity.showingTabList,
                hiddenTabList = entity.hiddenTabList,
            )

            ContentType.BLUESKY -> ContentConfig.BlueskyContent(
                id = entity.id,
                order = entity.order,
                name = entity.name,
                baseUrl = entity.baseUrl!!,
                tabList = emptyList(),
            )
        }
    }

    fun toEntity(config: ContentConfig): ContentConfigEntity {
        return when (config) {
            is ContentConfig.MixedContent -> ContentConfigEntity(
                id = config.id,
                order = config.order,
                name = config.name,
                type = ContentType.MIXED,
                sourceUriList = config.sourceUriList,
                baseUrl = null,
                showingTabList = emptyList(),
                hiddenTabList = emptyList()
            )

            is ContentConfig.ActivityPubContent -> ContentConfigEntity(
                id = config.id,
                name = config.name,
                order = config.order,
                type = ContentType.ACTIVITY_PUB,
                sourceUriList = null,
                baseUrl = config.baseUrl,
                showingTabList = config.showingTabList,
                hiddenTabList = config.hiddenTabList,
            )

            is ContentConfig.BlueskyContent -> ContentConfigEntity(
                id = config.id,
                name = config.name,
                order = config.order,
                type = ContentType.BLUESKY,
                sourceUriList = null,
                baseUrl = config.baseUrl,
                showingTabList = emptyList(),
                hiddenTabList = emptyList(),
            )
        }
    }
}