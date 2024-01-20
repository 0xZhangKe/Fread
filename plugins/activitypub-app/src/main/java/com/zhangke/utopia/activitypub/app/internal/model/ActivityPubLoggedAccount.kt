package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri

class ActivityPubLoggedAccount(
    val userId: String,
    uri: FormalUri,
    webFinger: WebFinger,
    platform: BlogPlatform,
    val baseUrl: FormalBaseUrl,
    name: String,
    description: String?,
    avatar: String?,
    val url: String,
    val token: ActivityPubTokenEntity,
) : LoggedAccount(
    uri = uri,
    webFinger = webFinger,
    platform = platform,
    userName = name,
    description = description,
    avatar = avatar,
)
