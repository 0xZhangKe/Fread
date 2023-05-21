package com.zhangke.utopia.status.resolvers

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusProviderUri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolve uri to BlogSource.
 */
@Singleton
class StatusSourceResolver @Inject constructor(
    private val resolvers: List<IStatusSourceResolver>
) {

    suspend fun resolve(uri: String): Result<StatusSource> {
        val errorResult = Result.failure<StatusSource>(
            IllegalArgumentException("invalidate $uri")
        )
        val uriString = StatusProviderUri.create(uri) ?: return errorResult
        return resolvers.firstOrNull { it.applicable(uriString) }?.resolve(uriString) ?: errorResult
    }
}

interface IStatusSourceResolver {

    fun applicable(uri: StatusProviderUri): Boolean

    suspend fun resolve(uri: StatusProviderUri): Result<StatusSource>
}
