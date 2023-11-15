package com.zhangke.utopia.activitypub.app.internal.account

import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.UtopiaPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubLoggedAccount(
    val userId: String,
    uri: StatusProviderUri,
    val webFinger: WebFinger,
    platform: UtopiaPlatform,
    val host: String,
    name: String,
    description: String?,
    avatar: String?,
    val homepage: String?,
    active: Boolean,
    val token: ActivityPubTokenEntity,
) : LoggedAccount(
    uri = uri,
    platform = platform,
    userName = name,
    description = description,
    avatar = avatar,
    active = active,
)
