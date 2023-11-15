package com.zhangke.utopia.activitypub.app.internal.account

import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.UtopiaPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubLoggedAccount(
    val userId: String,
    val uri: StatusProviderUri,
    val webFinger: WebFinger,
    val platform: UtopiaPlatform,
    val host: String,
    val name: String,
    val description: String?,
    val avatar: String?,
    val homepage: String?,
    val active: Boolean,
    val token: ActivityPubTokenEntity,
)
