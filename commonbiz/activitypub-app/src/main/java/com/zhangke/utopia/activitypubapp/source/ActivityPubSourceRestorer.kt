package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.BlogSourceRestorer
import com.zhangke.utopia.blogprovider.MetaSourceInfo

object ActivityPubSourceRestorer : BlogSourceRestorer {

    override fun restoreBlogSource(
        metaSourceInfo: MetaSourceInfo,
        sourceServer: String,
        protocol: String,
        sourceName: String,
        sourceDescription: String?,
        avatar: String?,
        extra: JsonObject?
    ): BlogSource? {
        if (protocol != ACTIVITY_PUB_PROTOCOL) return null
        return BlogSourceFactory.createBlogSource(
            metaSourceInfo = metaSourceInfo,
            sourceName = sourceName,
            sourceServer = sourceServer,
            sourceDescription = sourceDescription,
            avatar = avatar,
            protocol = protocol,
            extra = extra!!
        )
    }
}