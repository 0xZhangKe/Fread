package com.zhangke.utopia.activitypub.app.internal.account

import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.UtopiaPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubLoggedAccount(
    userId: String,
    uri: StatusProviderUri,
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
