package com.zhangke.utopia.status.resolvers

import com.zhangke.utopia.status.source.StatusSourceOwner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolve BlogSourceOwner by query.
 * supported:
 * - Source's uri
 * - Source's WebFinger
 * - url
 * - host
 */
@Singleton
class SourceMaintainerResolver @Inject constructor(
    private val resolvers: List<IStatusSourceOwnerResolver>,
) {

    suspend fun resolve(query: String): Result<StatusSourceOwner> {
        resolvers.forEachIndexed { index, item ->
            val source = item.resolve(query)
            if (source != null &&
                (source.isSuccess || index == resolvers.lastIndex)
            ) return source
        }
        return Result.failure(IllegalArgumentException("$query not found!"))
    }
}

interface IStatusSourceOwnerResolver {

    suspend fun resolve(content: String): Result<StatusSourceOwner>?
}
