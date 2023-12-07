package com.zhangke.utopia.status.source

import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusSourceResolver(
    private val resolverList: List<IStatusSourceResolver>,
) {

    suspend fun resolveSourceByUri(uri: StatusProviderUri): Result<StatusSource?>{
        resolverList.forEach {
            val result = it.resolveSourceByUri(uri)
            if (result.getOrNull() != null) return result
        }
        return Result.success(null)
    }
}

interface IStatusSourceResolver {

    suspend fun resolveSourceByUri(uri: StatusProviderUri): Result<StatusSource?>
}
