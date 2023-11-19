package com.zhangke.utopia.activitypub.app.internal.account.adapter

import com.zhangke.utopia.activitypub.app.internal.account.entities.BlogPlatformEntity
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class BlogPlatformEntityAdapter @Inject constructor() {

    fun toPlatform(entity: BlogPlatformEntity): BlogPlatform = BlogPlatform(
        uri = entity.uri,
        name = entity.name,
        description = entity.description,
        baseUrl = entity.baseUrl,
        thumbnail = entity.thumbnail,
        protocol = entity.protocol,
    )

    fun fromPlatform(platform: BlogPlatform) = BlogPlatformEntity(
        uri = platform.uri,
        name = platform.name,
        description = platform.description,
        baseUrl = platform.baseUrl,
        thumbnail = platform.thumbnail,
        protocol = platform.protocol,
    )
}
