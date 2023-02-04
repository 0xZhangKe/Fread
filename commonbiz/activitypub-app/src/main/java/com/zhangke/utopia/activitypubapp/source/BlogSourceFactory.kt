package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonPrimitive
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.source.TimelineSource.Companion.newInstance
import com.zhangke.utopia.activitypubapp.source.UserSource.Companion.newInstance
import com.zhangke.utopia.blogprovider.BlogSource

internal object BlogSourceFactory {

    fun BlogSourceScope.createBlogSource(): BlogSource {
        val type = extra.get("type")
            ?.let { if (it is JsonPrimitive) it.asString else null }
            ?.runCatching { ActivityPubSourceType.valueOf(this) }?.getOrNull()
            ?: throw IllegalArgumentException("$extra is not a ActivityPubBlogSource!")
        return when (type) {
            ActivityPubSourceType.PUBLIC_TIMELINE,
            ActivityPubSourceType.LOCAL_TIMELINE,
            ActivityPubSourceType.HOME_TIMELINE -> {
                val timelineExtra = globalGson.fromJson(extra, TimelineSourceExtra::class.java)
                newInstance(timelineExtra)
            }
            ActivityPubSourceType.USER_STATUS, ActivityPubSourceType.USER_STATUS_EXCLUDE_REPLIES -> {
                val userSourceExtra = globalGson.fromJson(extra, UserSourceExtra::class.java)
                newInstance(userSourceExtra)
            }
        }
    }
}