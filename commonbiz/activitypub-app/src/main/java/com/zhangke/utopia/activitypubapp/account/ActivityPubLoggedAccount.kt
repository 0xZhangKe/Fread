package com.zhangke.utopia.activitypubapp.account

import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.platform.UtopiaPlatform

data class ActivityPubLoggedAccount(
    val userId: String,
    val uri: String,
    val webFinger: WebFinger,
    val platform: UtopiaPlatform,
    val host: String,
    val name: String,
    val description: String?,
    val avatar: String?,
    val homepage: String?,
    val active: Boolean,
    val validate: Boolean,
    val token: ActivityPubTokenEntity,
)
