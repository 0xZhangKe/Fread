package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypubapp.newActivityPubClient
import com.zhangke.utopia.activitypubapp.source.TimelineSource.Companion.newInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubApplicableUrl
import com.zhangke.utopia.activitypubapp.utils.toMetaSource
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.MetaSourceInfo

internal class TimelineSource(
    val isLocal: Boolean,
    metaSourceInfo: MetaSourceInfo,
    sourceServer: String,
    protocol: String,
    sourceName: String,
    sourceDescription: String?,
    avatar: String?,
    extra: JsonObject,
) : BlogSource(
    metaSourceInfo = metaSourceInfo,
    sourceServer = sourceServer,
    protocol = protocol,
    sourceName = sourceName,
    sourceDescription = sourceDescription,
    avatar = avatar,
    extra = extra
) {

    companion object {

        fun BlogSourceScope.newInstance(timelineSourceExtra: TimelineSourceExtra): TimelineSource {
            val isLocal = timelineSourceExtra.type == ActivityPubSourceType.LOCAL_TIMELINE
            return TimelineSource(
                isLocal = isLocal,
                metaSourceInfo = metaSourceInfo,
                sourceServer = sourceServer,
                protocol = protocol,
                sourceName = sourceName,
                sourceDescription = sourceDescription,
                avatar = avatar,
                extra = extra
            )
        }
    }
}

internal class TimelineSourceExtra(
    val type: ActivityPubSourceType
)

internal class TimelineSourceResolver(private val isLocal: Boolean) : ActivityPubSourceInternalResolver {

    override suspend fun resolve(
        url: ActivityPubApplicableUrl,
        instance: ActivityPubInstance
    ): BlogSource {
        return instance.toTimelineSource(isLocal)
    }

    private fun ActivityPubInstance.toTimelineSource(isLocal: Boolean): TimelineSource {
        val type = if (isLocal) ActivityPubSourceType.LOCAL_TIMELINE
        else ActivityPubSourceType.PUBLIC_TIMELINE
        val extra = TimelineSourceExtra(type)
        val scope = BlogSourceScope(
            metaSourceInfo = toMetaSource(),
            sourceServer = domain,
            sourceDescription = description,
            sourceName = title,
            avatar = thumbnail.url,
            protocol = ACTIVITY_PUB_PROTOCOL,
            extra = globalGson.toJsonTree(extra).asJsonObject
        )
        return scope.newInstance(extra)
    }
}