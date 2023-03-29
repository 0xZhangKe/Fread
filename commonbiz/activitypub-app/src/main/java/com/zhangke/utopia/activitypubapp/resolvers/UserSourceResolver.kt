package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.utopia.activitypubapp.source.user.UserSourceRepo
import com.zhangke.utopia.activitypubapp.source.user.getUserWebFinger
import com.zhangke.utopia.activitypubapp.source.user.isUserSource
import com.zhangke.utopia.status.resolvers.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceUri
import javax.inject.Inject

internal class UserSourceResolver @Inject constructor() : IStatusSourceResolver {

    override fun applicable(uri: StatusSourceUri): Boolean {
        return uri.isUserSource()
    }

    override suspend fun resolve(uri: StatusSourceUri): StatusSource? {
        val webFinger = uri.getUserWebFinger()
            ?: throw IllegalArgumentException("$uri is not a UserSource!")
        return UserSourceRepo.query(webFinger)
    }
}