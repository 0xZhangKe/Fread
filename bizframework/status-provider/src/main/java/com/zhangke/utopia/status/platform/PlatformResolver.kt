package com.zhangke.utopia.status.platform

import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.model.StatusProviderProtocol
import com.zhangke.utopia.status.uri.FormalUri

class PlatformResolver(
    private val resolverList: List<IPlatformResolver>,
) {

    suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform> {
        return resolverList.mapFirst { it.resolve(blogSnapshot) }
    }

    suspend fun getSuggestedPlatformList(): List<PlatformSnapshot> {
        return resolverList.map { it.getSuggestedPlatformSnapshotList() }.flatten()
    }

    suspend fun resolveBySourceUriList(uriList: List<FormalUri>): Result<List<BlogPlatform>> {
        val resultList = uriList.map { uri ->
            resolverList.mapFirst { it.resolveBySourceUri(uri) }
        }
        val platformList = resultList.mapNotNull { it.getOrNull() }.distinct()
        if (platformList.isNotEmpty()) return Result.success(platformList)
        val exception = resultList.mapFirstOrNull { it.exceptionOrNull() }
        if (exception != null) return Result.failure(exception)
        return Result.success(emptyList())
    }
}

interface IPlatformResolver {

    suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>?

    suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot>

    suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?>
}
