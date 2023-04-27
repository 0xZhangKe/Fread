package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.utopia.activitypubapp.source.user.UserSourceRepo
import com.zhangke.utopia.activitypubapp.source.user.getUserWebFinger
import com.zhangke.utopia.activitypubapp.source.user.isUserSource
import com.zhangke.utopia.status.resolvers.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceUri
import javax.inject.Inject

internal class UserSourceResolver @Inject constructor(
    private val repo: UserSourceRepo,
) : IStatusSourceResolver {

    override fun applicable(uri: StatusSourceUri): Boolean {
        return uri.isUserSource()
    }

    override suspend fun resolve(uri: StatusSourceUri): Result<StatusSource> {
        val webFinger = uri.getUserWebFinger() ?: return Result.failure(
            IllegalArgumentException("$uri is not a UserSource!")
        )
        return repo.query(webFinger)?.let { Result.success(it) } ?: Result.failure(
            IllegalArgumentException("$uri not found!")
        )
    }
}