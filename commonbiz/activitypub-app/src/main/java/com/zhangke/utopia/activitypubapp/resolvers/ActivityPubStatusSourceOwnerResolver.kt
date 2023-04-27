package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.resolvers.IStatusSourceOwnerResolver
import com.zhangke.utopia.status.source.StatusSourceOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityPubStatusSourceOwnerResolver @Inject constructor(
    private val resolvers: List<IActivityPubSourceMaintainerResolver>
) : IStatusSourceOwnerResolver {

    override suspend fun resolve(content: String): Result<StatusSourceOwner>? {
        return resolvers.mapFirstOrNull { it.resolve(content) }
    }
}

interface IActivityPubSourceMaintainerResolver {

    suspend fun resolve(query: String): Result<StatusSourceOwner>?
}
