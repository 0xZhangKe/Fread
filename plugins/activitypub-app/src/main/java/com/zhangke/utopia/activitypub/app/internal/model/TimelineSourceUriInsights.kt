package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.status.uri.FormalUri

data class TimelineSourceUriInsights(
    val uri: FormalUri,
    val serverBaseUrl: FormalBaseUrl,
    val type: TimelineSourceType,
)

enum class TimelineSourceType {

    PUBLIC,
    LOCAL,
    HOME;

    val nickName: String
        get() = when (this) {
            HOME -> appContext.getString(R.string.activity_pub_home_timeline)
            LOCAL -> appContext.getString(R.string.activity_pub_local_timeline)
            PUBLIC -> appContext.getString(R.string.activity_pub_public_timeline)
        }

    companion object {

        fun valurOfOrNull(name: String): TimelineSourceType? {
            return entries.firstOrNull { it.name == name }
        }
    }
}
