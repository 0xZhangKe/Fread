package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.IStatusSourceResolver
import com.zhangke.utopia.status_provider.StatusSourceUri

internal class UserSourceResolver : IStatusSourceResolver {

    override fun applicable(uri: StatusSourceUri): Boolean {
        return getUserWebFinger(uri) != null
    }

    override suspend fun resolve(uri: StatusSourceUri): StatusSource? {
        val webFinger = getUserWebFinger(uri)
            ?: throw IllegalArgumentException("$uri is not a UserSource!")
        return UserSourceRepo.query(webFinger)
    }
}