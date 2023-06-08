package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.utopia.activitypubapp.protocol.isUserSource
import com.zhangke.utopia.activitypubapp.protocol.parseUserInfo
import com.zhangke.utopia.activitypubapp.source.user.UserSourceRepo
import com.zhangke.utopia.status.resolvers.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusProviderUri
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

internal class UserSourceResolver @Inject constructor(
    private val repo: UserSourceRepo,
) : IStatusSourceResolver {

    override fun applicable(uri: StatusProviderUri): Boolean {
        return uri.isUserSource()
    }

    override suspend fun resolve(uri: StatusProviderUri): Result<StatusSource> {
        val (webFinger, userId) = uri.parseUserInfo() ?: return Result.failure(
            IllegalArgumentException("$uri is not a UserSource!")
        )
        return repo.query(webFinger)?.let { Result.success(it) } ?: Result.failure(
            IllegalArgumentException("$uri not found!")
        )
    }
}