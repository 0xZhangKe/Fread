package com.zhangke.utopia.status.platform

import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.uri.FormalUri

class PlatformResolver constructor(
    private val resolverList: List<IPlatformResolver>,
) {

    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun resolveBySourceUri(uri: FormalUri): Result<BlogPlatform?> {
        return resolverList.mapFirst { it.resolveBySourceUri(uri) }
    }

    suspend fun resolveBySourceUriList(uriList: List<FormalUri>): Result<List<BlogPlatform>> {
        val resultList = uriList.map { resolveBySourceUri(it) }
        val platformList = resultList.mapNotNull { it.getOrNull() }.distinct()
        if (platformList.isNotEmpty()) return Result.success(platformList)
        val exception = resultList.mapFirstOrNull { it.exceptionOrNull() }
        if (exception != null) return Result.failure(exception)
        return Result.success(emptyList())
    }

    suspend fun getAllRecordedPlatform(): List<BlogPlatform> {
        return resolverList.flatMap { it.getAllRecordedPlatform() }
    }
}

interface IPlatformResolver {

    suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?>

    suspend fun getAllRecordedPlatform(): List<BlogPlatform>
}
