package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.R

enum class ActivityPubTimelineType {

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

        fun valurOfOrNull(name: String): ActivityPubTimelineType? {
            return entries.firstOrNull { it.name == name }
        }
    }
}
