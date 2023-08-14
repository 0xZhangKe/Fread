package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.uri.user.ActivityPubUserUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri

class UserSource(
    val userId: String,
    val webFinger: WebFinger,
    override val name: String,
    override val description: String,
    override val thumbnail: String?,
) : StatusSource {

    override val uri: StatusProviderUri = ActivityPubUserUri.create(userId, webFinger)
        .toStatusProviderUri()
}
