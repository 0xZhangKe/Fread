package com.zhangke.utopia.activitypubapp.source.timeline

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.R
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusSourceMaintainer

internal class TimelineSource(
    val host: String,
    val type: TimelineSourceType,
) : StatusSource {

    override val uri: String = buildTimelineSourceUri(host, type).toString()

    override val nickName: String = type.nickName

    override val description: String = ""

    override val thumbnail: String? = null

    override suspend fun saveToLocal() {
        TimelineRepo.save(this)
    }

    override suspend fun requestMaintainer(): StatusSourceMaintainer {
        return TimelineSourceMaintainerResolver.resolveByHost(host)
    }
}

internal enum class TimelineSourceType(val stringValue: String) {

    PUBLIC("public"),
    LOCAL("local"),
    HOME("home");

    val nickName: String
        get() = when (this) {
            HOME -> appContext.getString(R.string.activity_pub_home_timeline)
            LOCAL -> appContext.getString(R.string.activity_pub_local_timeline)
            PUBLIC -> appContext.getString(R.string.activity_pub_public_timeline)
        }
}