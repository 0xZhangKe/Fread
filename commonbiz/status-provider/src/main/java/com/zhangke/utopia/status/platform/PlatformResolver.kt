package com.zhangke.utopia.status.platform

import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.uri.StatusProviderUri

class PlatformResolver constructor(
    private val resolverList: List<IPlatformResolver>,
) {

    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun resolveBySourceUri(uri: String): Result<BlogPlatform?> {
        val statusProviderUri = StatusProviderUri.from(uri) ?: return Result.success(null)
        return resolverList.mapFirst { it.resolveBySourceUri(statusProviderUri) }
    }

    suspend fun resolveBySourceUriList(uriList: List<String>): Result<List<BlogPlatform>> {
        val resultList = uriList.map { resolveBySourceUri(it) }
        val platformList = resultList.mapNotNull { it.getOrNull() }
        if (platformList.isNotEmpty()) return Result.success(platformList)
        val exception = resultList.mapFirstOrNull { it.exceptionOrNull() }
        if (exception != null) return Result.failure(exception)
        return Result.success(emptyList())
    }
}

interface IPlatformResolver {

    suspend fun resolveBySourceUri(sourceUri: StatusProviderUri): Result<BlogPlatform?>
}
