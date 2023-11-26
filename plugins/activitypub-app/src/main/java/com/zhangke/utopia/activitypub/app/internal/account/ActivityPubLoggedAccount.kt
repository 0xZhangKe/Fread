package com.zhangke.utopia.activitypub.app.internal.account

import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.BlogPlatform

class ActivityPubLoggedAccount(
    val userId: String,
    uri: ActivityPubUserUri,
    webFinger: WebFinger,
    platform: BlogPlatform,
    val host: String,
    name: String,
    description: String?,
    avatar: String?,
    val homepage: String?,
    active: Boolean,
    val token: ActivityPubTokenEntity,
) : LoggedAccount(
    uri = uri,
    webFinger = webFinger,
    platform = platform,
    userName = name,
    description = description,
    avatar = avatar,
    active = active,
)
