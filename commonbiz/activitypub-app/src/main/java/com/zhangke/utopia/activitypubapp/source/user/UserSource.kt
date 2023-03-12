package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceMaintainerResolver
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusSourceMaintainer

internal class UserSource(
    override val nickName: String,
    override val description: String,
    override val thumbnail: String?,
    val webFinger: WebFinger,
) : StatusSource {

    override val uri: String = buildUserSourceUri(webFinger).toString()

    override suspend fun saveToLocal() {
        UserSourceRepo.save(this)
    }

    override suspend fun requestMaintainer(): StatusSourceMaintainer {
        return TimelineSourceMaintainerResolver.resolveByHost(webFinger.host)
    }
}