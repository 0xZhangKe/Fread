package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.source.TimelineSourceInterpreter
import com.zhangke.utopia.activitypubapp.source.UserSourceInterpreter
import com.zhangke.utopia.activitypubapp.utils.ActivityPubApplicableUrl
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.BlogSourceGroup
import com.zhangke.utopia.blogprovider.BlogSourceInterpreter
import com.zhangke.utopia.blogprovider.MetaSourceInfo

object ActivityPubSourceInterpreter : BlogSourceInterpreter {

    private val blogSourceInterpreterList = listOf(
        UserSourceInterpreter(),
        TimelineSourceInterpreter(isLocal = true),
        TimelineSourceInterpreter(isLocal = false),
    )

    private val uriToInstanceCache = mutableMapOf<String, ActivityPubInstance>()
    private val uriToApplicableCache = mutableMapOf<String, Boolean>()
    private val uriToBlogSourceGroup = mutableMapOf<String, BlogSourceGroup>()

    override suspend fun applicable(uri: String): Boolean {
        if (uriToApplicableCache.containsKey(uri)) return uriToApplicableCache[uri]!!
        val url = ActivityPubApplicableUrl(uri)
        val applicable = if (url.validate()) {
            var instance = uriToInstanceCache[uri]
            if (instance == null) {
                instance = getInstanceOrNull(url)?.apply {
                    uriToInstanceCache[uri] = this
                }
            }
            if (instance != null) {
                val applicableInterpreter =
                    blogSourceInterpreterList.firstOrNull { it.applicable(url, instance) }
                applicableInterpreter != null
            } else {
                false
            }
        } else {
            false
        }
        uriToApplicableCache[uri] = applicable
        return applicable
    }

    /**
     * Call this method must ensure this interpreter is applicable.
     */
    override suspend fun createSourceGroup(uri: String): BlogSourceGroup {
        if (uriToBlogSourceGroup.containsKey(uri)) return uriToBlogSourceGroup[uri]!!
        val url = ActivityPubApplicableUrl(uri)
        val instance = uriToInstanceCache.getOrPut(uri) { getInstanceOrNull(url)!! }
        val sourceList = blogSourceInterpreterList.filter { it.applicable(url, instance) }
            .map { it.createSource(url, instance) }
        return BlogSourceGroup(
            metaSourceInfo = instance.toMetaSource(),
            sourceList = sourceList,
        ).apply {
            uriToBlogSourceGroup[uri] = this
        }
    }

    // fixme 思考下这个方法的设计
    override suspend fun validate(source: BlogSource): Boolean {
        if (source.protocol != ACTIVITY_PUB_PROTOCOL) return false
        val timeline = newActivityPubClient(source.sourceServer)
            .timelinesRepo
            .localTimelines()
            .getOrNull()
        return timeline != null
    }

    private suspend fun getInstanceOrNull(url: ActivityPubApplicableUrl): ActivityPubInstance? {
        return newActivityPubClient(url.host!!)
            .instanceRepo
            .getInstanceInformation()
            .getOrNull()
    }

    private fun ActivityPubInstance.toSource(): BlogSource {
        return BlogSource(
            metaSourceInfo = toMetaSource(),
            sourceServer = domain,
            sourceDescription = description,
            sourceName = title,
            avatar = thumbnail.url,
            protocol = ACTIVITY_PUB_PROTOCOL,
            extra = null,
        )
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
}