package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.utopia.activitypubapp.source.user.UserSourceRepo
import com.zhangke.utopia.activitypubapp.protocol.getUserWebFinger
import com.zhangke.utopia.activitypubapp.protocol.isUserSource
import com.zhangke.utopia.status.resolvers.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusProviderUri
import javax.inject.Inject

internal class UserSourceResolver @Inject constructor(
    private val repo: UserSourceRepo,
) : IStatusSourceResolver {

    override fun applicable(uri: StatusProviderUri): Boolean {
        return uri.isUserSource()
    }

    override suspend fun resolve(uri: StatusProviderUri): Result<StatusSource> {
        val webFinger = uri.getUserWebFinger() ?: return Result.failure(
            IllegalArgumentException("$uri is not a UserSource!")
        )
        return repo.query(webFinger)?.let { Result.success(it) } ?: Result.failure(
            IllegalArgumentException("$uri not found!")
        )
    }
}