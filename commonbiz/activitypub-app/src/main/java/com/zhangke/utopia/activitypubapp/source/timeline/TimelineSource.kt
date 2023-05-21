package com.zhangke.utopia.activitypubapp.source.timeline

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.R
import com.zhangke.utopia.activitypubapp.protocol.buildTimelineSourceUri
import com.zhangke.utopia.status.source.StatusSource

internal class TimelineSource(
    val host: String,
    val type: TimelineSourceType,
) : StatusSource {

    override val uri: String = buildTimelineSourceUri(host, type).toString()

    override val name: String = type.nickName

    override val description: String = ""

    override val thumbnail: String? = null
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

    companion object {

        fun valurOfOrNull(stringValue: String): TimelineSourceType? {
            return values().firstOrNull { it.stringValue == stringValue }
        }
    }
}
