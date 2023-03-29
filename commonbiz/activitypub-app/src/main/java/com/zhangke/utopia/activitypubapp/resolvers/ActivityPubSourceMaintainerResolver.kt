package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.resolvers.ISourceMaintainerResolver
import com.zhangke.utopia.status.source.StatusSourceMaintainer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityPubSourceMaintainerResolver @Inject constructor(
    private val resolvers: List<IActivityPubSourceMaintainerResolver>
) : ISourceMaintainerResolver {

    override suspend fun resolve(content: String): StatusSourceMaintainer? {
        return resolvers.mapFirstOrNull { it.resolve(content) }
    }
}

interface IActivityPubSourceMaintainerResolver {

    suspend fun resolve(query: String): StatusSourceMaintainer?
}