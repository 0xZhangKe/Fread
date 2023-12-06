package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.status.uri.StatusProviderUri

data class TimelineSourceUriData(
    val uri: StatusProviderUri,
    val serverBaseUrl: String,
    val type: TimelineSourceType,
)

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
