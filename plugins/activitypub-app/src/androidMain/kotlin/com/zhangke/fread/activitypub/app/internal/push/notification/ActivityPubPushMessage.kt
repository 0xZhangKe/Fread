package com.zhangke.fread.activitypub.app.internal.push.notification

import com.zhangke.fread.status.account.LoggedAccount

data class ActivityPubPushMessage(
    val accessToken: String?,
    val preferredLocale: String?,
    val notificationId: Long?,
    val account: LoggedAccount?,
    val notificationType: Type,
    val icon: String,
    val title: String,
    val body: String,
) {

    enum class Type {

        FAVORITE,
        MENTION,
        REBLOG,
        FOLLOW,
        POLL;

        companion object {

            fun fromName(name: String): Type? {
                return when (name) {
                    "favourite" -> FAVORITE
                    "mention" -> MENTION
                    "reblog" -> REBLOG
                    "follow" -> FOLLOW
                    "poll" -> POLL
                    else -> null
                }
            }
        }
    }
}
