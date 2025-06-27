package com.zhangke.fread.status.source

import com.zhangke.framework.collections.mapFirst
import com.zhangke.fread.status.uri.FormalUri

class StatusSourceResolver(
    private val resolverList: List<IStatusSourceResolver>,
) {

    suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?> {
        resolverList.forEach {
            val result = it.resolveSourceByUri(uri)
            if (result.getOrNull() != null) return result
        }
        return Result.success(null)
    }

    suspend fun resolveRssSource(rssUrl: String): Result<StatusSource> {
        return resolverList.mapFirst { it.resolveRssSource(rssUrl) }
    }
}

interface IStatusSourceResolver {

    suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?>

    suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>?
}
