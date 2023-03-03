package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.TimelineSource.Companion.newInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.BlogSourceGroup
import com.zhangke.utopia.status_provider.BlogSourceResolver
import com.zhangke.utopia.status_provider.MetaSourceInfo

internal class TimelineSource(
    val type: ActivityPubSourceType,
    metaSourceInfo: MetaSourceInfo,
    val domain: String,
    protocol: String,
    sourceName: String,
    sourceDescription: String?,
    avatar: String?,
    extra: JsonObject,
) : StatusSource(
    metaSourceInfo = metaSourceInfo,
    uri = domain,
    protocol = protocol,
    sourceName = sourceName,
    sourceDescription = sourceDescription,
    avatar = avatar,
    extra = extra
) {

    companion object {

        fun BlogSourceScope.newInstance(timelineSourceExtra: TimelineSourceExtra): TimelineSource {
            return TimelineSource(
                type = timelineSourceExtra.type,
                metaSourceInfo = metaSourceInfo,
                domain = uri,
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

// This will provides Public/Local/Home timeline sources.
internal class TimelineSourceResolver : BlogSourceResolver {

    private val instanceCache = mutableMapOf<ActivityPubUrl, ActivityPubInstance>()

    override suspend fun resolve(content: String): BlogSourceGroup? {
        val url = ActivityPubUrl(content)
        if (!url.validate()) return null
        val instance = requestActivityPubInstance(url) ?: return null
        val sourceList = listOf(
            instance.toTimelineSource(ActivityPubSourceType.LOCAL_TIMELINE),
            instance.toTimelineSource(ActivityPubSourceType.PUBLIC_TIMELINE),
            instance.toTimelineSource(ActivityPubSourceType.HOME_TIMELINE),
        )
        return BlogSourceGroup(
            metaSourceInfo = instance.toMetaSource(),
            sourceList = sourceList
        )
    }

    private suspend fun requestActivityPubInstance(url: ActivityPubUrl): ActivityPubInstance? {
        return instanceCache[url] ?: obtainActivityPubClient(url.toughHost).instanceRepo
            .getInstanceInformation()
            .getOrNull()
            ?.also { instanceCache[url] = it }
    }

    private fun ActivityPubInstance.toMetaSource(): MetaSourceInfo {
        return MetaSourceInfo(
            url = domain,
            name = title,
            thumbnail = thumbnail.url,
            description = description,
            extra = null
        )
    }

    private fun ActivityPubInstance.toTimelineSource(type: ActivityPubSourceType): TimelineSource {
        val extra = TimelineSourceExtra(type)
        val scope = BlogSourceScope(
            metaSourceInfo = toMetaSource(),
            uri = domain,
            sourceDescription = description,
            sourceName = title,
            avatar = thumbnail.url,
            protocol = ACTIVITY_PUB_PROTOCOL,
            extra = globalGson.toJsonTree(extra).asJsonObject
        )
        return scope.newInstance(extra)
    }
}