package com.zhangke.utopia.activitypubapp.account

import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.UtopiaPlatform

class ActivityPubLoggedAccount(
    userId: String,
    uri: String,
    val webFinger: WebFinger,
    platform: UtopiaPlatform,
    host: String,
    name: String,
    description: String?,
    avatar: String?,
    homepage: String?,
    active: Boolean,
    val token: ActivityPubTokenEntity,
) : LoggedAccount(
    userId = userId,
    uri = uri,
    platform = platform,
    host = host,
    name = name,
    description = description,
    avatar = avatar,
    homepage = homepage,
    active = active,
)
