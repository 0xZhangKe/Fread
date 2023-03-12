package com.zhangke.utopia.status_provider

/**
 * Resolve uri to BlogSource.
 */
object StatusSourceResolver {

    private val resolvers: List<IStatusSourceResolver> = ImplementationFinder().findImplementation()

    suspend fun resolve(sourceUri: String): StatusSource? {
        val uri = StatusSourceUri.create(sourceUri) ?: return null
        return resolvers.firstOrNull { it.applicable(uri) }?.resolve(uri)
    }
}

interface IStatusSourceResolver {

    fun applicable(uri: StatusSourceUri): Boolean

    suspend fun resolve(uri: StatusSourceUri): StatusSource?
}
