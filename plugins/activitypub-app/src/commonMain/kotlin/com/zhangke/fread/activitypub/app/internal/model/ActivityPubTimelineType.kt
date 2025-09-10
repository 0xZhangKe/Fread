package com.zhangke.fread.activitypub.app.internal.model

import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.getString

enum class ActivityPubTimelineType {

    PUBLIC,
    LOCAL,
    HOME;

    suspend fun nickName(): String {
        return when (this) {
            HOME -> getString(LocalizedString.activity_pub_home_timeline)
            LOCAL -> getString(LocalizedString.activity_pub_local_timeline)
            PUBLIC -> getString(LocalizedString.activity_pub_public_timeline)
        }
    }

    companion object {

        fun valurOfOrNull(name: String): ActivityPubTimelineType? {
            return entries.firstOrNull { it.name == name }
        }
    }
}
