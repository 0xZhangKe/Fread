package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypubapp.newActivityPubClient
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

        fun newInstance(
            metaSourceInfo: MetaSourceInfo,
            sourceServer: String,
            protocol: String,
            sourceName: String,
            sourceDescription: String?,
            avatar: String?,
            extra: JsonObject,
            timelineSourceExtra: TimelineSourceExtra
        ): TimelineSource {
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

internal class TimelineSourceInterpreter(private val isLocal: Boolean) :
    ActivityPubInternalSourceInterpreter {

    override suspend fun applicable(
        url: ActivityPubApplicableUrl, instance: ActivityPubInstance
    ): Boolean {
        return url.query.isNullOrEmpty() && url.path.isNullOrEmpty()
    }

    override suspend fun createSource(
        url: ActivityPubApplicableUrl, instance: ActivityPubInstance
    ): BlogSource {
        return instance.toTimelineSource(isLocal)
    }

    private fun ActivityPubInstance.toTimelineSource(isLocal: Boolean): TimelineSource {
        val type =
            if (isLocal) ActivityPubSourceType.LOCAL_TIMELINE
            else ActivityPubSourceType.PUBLIC_TIMELINE
        val extra = TimelineSourceExtra(type)
        return TimelineSource.newInstance(
            metaSourceInfo = toMetaSource(),
            sourceServer = domain,
            sourceDescription = description,
            sourceName = title,
            avatar = thumbnail.url,
            protocol = ACTIVITY_PUB_PROTOCOL,
            extra = globalGson.toJsonTree(extra).asJsonObject,
            timelineSourceExtra = extra
        )
    }

    override suspend fun validate(source: BlogSource): Boolean {
        if (source !is TimelineSource) return false
        val timeline = newActivityPubClient(source.sourceServer).timelinesRepo.run {
            if (source.isLocal) {
                localTimelines()
            } else {
                publicTimelines()
            }
        }.getOrNull()
        return timeline != null
    }
}