package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri

class UserSource(
    val userId: String,
    val webFinger: WebFinger,
    override val name: String,
    override val description: String,
    override val thumbnail: String?,
) : StatusSource {

    override val uri: StatusProviderUri get() = ActivityPubUserUri.create(userId, webFinger)
}
