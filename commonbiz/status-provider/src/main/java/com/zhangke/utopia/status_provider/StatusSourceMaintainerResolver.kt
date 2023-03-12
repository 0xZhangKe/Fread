package com.zhangke.utopia.status_provider

/**
 * try resolve any String to BlogSourceMaintainer.
 */
object StatusSourceMaintainerResolver {

    private val resolvers: List<IStatusSourceMaintainerResolver> =
        ImplementationFinder().findImplementation()

    suspend fun resolve(content: String): StatusSourceMaintainer? {
        val blogSourceByUri = StatusSourceResolver.resolve(content)
        if (blogSourceByUri != null) return blogSourceByUri.requestMaintainer()
        resolvers.forEach {
            val source = it.resolve(content)
            if (source != null) return source
        }
        return null
    }
}

interface IStatusSourceMaintainerResolver {

    suspend fun resolve(content: String): StatusSourceMaintainer?
}