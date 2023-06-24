package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.uri.user.ActivityPubUserUri
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.source.StatusSource

class UserSource(
    val userId: String,
    val webFinger: WebFinger,
    override val name: String,
    override val description: String,
    override val thumbnail: String?,
) : StatusSource {

    override val uri: String = ActivityPubUserUri.create(userId, webFinger).toString()
}
