package com.zhangke.utopia.status.resolvers

import com.zhangke.utopia.status.source.StatusSourceMaintainer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolve BlogSourceMaintainer by query.
 * supported:
 * - Source's uri
 * - Source's WebFinger
 * - url
 * - host
 */
@Singleton
class SourceMaintainerResolver @Inject constructor(
    private val resolvers: List<ISourceMaintainerResolver>,
) {

    suspend fun resolve(query: String): StatusSourceMaintainer? {
        resolvers.forEach {
            val source = it.resolve(query)
            if (source != null) return source
        }
        return null
    }
}

interface ISourceMaintainerResolver {

    suspend fun resolve(content: String): StatusSourceMaintainer?
}
