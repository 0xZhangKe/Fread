package com.zhangke.utopia.activitypubapp.source.timeline

import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.IStatusSourceResolver
import com.zhangke.utopia.status_provider.StatusSourceUri

class TimelineSourceResolver : IStatusSourceResolver {

    override fun applicable(uri: StatusSourceUri): Boolean {
        return getServerAndType(uri) != null
    }

    override suspend fun resolve(uri: StatusSourceUri): StatusSource? {
        val (serverUrl, type) = getServerAndType(uri)
            ?: throw IllegalArgumentException("$uri is not a timeline source!")
        return TimelineRepo.query(serverUrl.host, type)
    }
}