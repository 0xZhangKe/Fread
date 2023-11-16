package com.zhangke.utopia.status.source

import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusSourceResolver(
    private val resolverList: List<IStatusSourceResolver>,
) {

    suspend fun resolveSourceByUri(uri: String): Result<StatusSource?>{
        val statusProviderUri = StatusProviderUri.from(uri) ?: return Result.success(null)
        resolverList.forEach {
            val result = it.resolveSourceByUri(statusProviderUri)
            if (result.getOrNull() != null) return result
        }
        return Result.success(null)
    }
}

interface IStatusSourceResolver {

    suspend fun resolveSourceByUri(uri: StatusProviderUri): Result<StatusSource?>
}
