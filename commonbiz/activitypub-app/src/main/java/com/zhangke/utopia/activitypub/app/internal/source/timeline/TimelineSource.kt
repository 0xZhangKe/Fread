package com.zhangke.utopia.activitypub.app.internal.source.timeline

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.R
import com.zhangke.utopia.activitypub.app.internal.uri.timeline.ActivityTimelineUri
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri

class TimelineSource(
    val host: String,
    val type: TimelineSourceType,
    override val description: String = ""
) : StatusSource {

    override val uri: StatusProviderUri = ActivityTimelineUri.create(host, type)
        .toStatusProviderUri()

    override val name: String = type.nickName

    override val thumbnail: String? = null
}

enum class TimelineSourceType(val stringValue: String) {

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
