package com.zhangke.utopia.status_provider

/**
 * try resolve any String to BlogSourceMaintainer.
 */
object SourceMaintainerResolver {

    private val resolvers: List<ISourceMaintainerResolver> =
        ImplementerFinder().findImplementer()

    suspend fun resolve(query: String): StatusSourceMaintainer? {
        val blogSourceByUri = StatusSourceResolver.resolve(query)
        if (blogSourceByUri != null) return blogSourceByUri.requestMaintainer()
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