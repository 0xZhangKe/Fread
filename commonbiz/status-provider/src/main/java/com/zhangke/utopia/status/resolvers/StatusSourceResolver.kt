package com.zhangke.utopia.status.resolvers

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceUri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolve uri to BlogSource.
 */
@Singleton
class StatusSourceResolver @Inject constructor(
    private val resolvers: List<IStatusSourceResolver>
) {

    suspend fun resolve(uri: String): StatusSource? {
        val uriString = StatusSourceUri.create(uri) ?: return null
        return resolvers.firstOrNull { it.applicable(uriString) }?.resolve(uriString)
    }
}

interface IStatusSourceResolver {

    fun applicable(uri: StatusSourceUri): Boolean

    suspend fun resolve(uri: StatusSourceUri): StatusSource?
}
