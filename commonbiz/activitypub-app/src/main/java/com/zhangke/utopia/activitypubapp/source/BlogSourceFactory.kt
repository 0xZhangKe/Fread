package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.MetaSourceInfo

internal object BlogSourceFactory {

    fun createBlogSource(
        metaSourceInfo: MetaSourceInfo,
        sourceServer: String,
        protocol: String,
        sourceName: String,
        sourceDescription: String?,
        avatar: String?,
        extra: JsonObject,
    ): BlogSource {
        val type = extra.get("type")
            ?.let { if (it is JsonPrimitive) it.asString else null }
            ?.runCatching { ActivityPubSourceType.valueOf(this) }?.getOrNull()
            ?: throw IllegalArgumentException("$extra is not a ActivityPubBlogSource!")
        return when (type) {
            ActivityPubSourceType.PUBLIC_TIMELINE, ActivityPubSourceType.LOCAL_TIMELINE -> {
                val timelineExtra = globalGson.fromJson(extra, TimelineSourceExtra::class.java)
                TimelineSource.newInstance(
                    metaSourceInfo = metaSourceInfo,
                    sourceServer = sourceServer,
                    protocol = protocol,
                    sourceName = sourceName,
                    sourceDescription = sourceDescription,
                    avatar = avatar,
                    extra = extra,
                    timelineSourceExtra = timelineExtra
                )
            }
            ActivityPubSourceType.USER -> {
                val userSourceExtra = globalGson.fromJson(extra, UserSourceExtra::class.java)
                UserSource.newInstance(
                    metaSourceInfo = metaSourceInfo,
                    sourceServer = sourceServer,
                    protocol = protocol,
                    sourceName = sourceName,
                    sourceDescription = sourceDescription,
                    avatar = avatar,
                    extra = extra,
                    userSourceExtra = userSourceExtra
                )
            }
        }
    }
}