package com.zhangke.utopia.status_provider

import okhttp3.internal.toImmutableList
import java.util.*

object StatusSourceResolver {

    private val resolvers: List<StatusSourceUriResolver> =
        with(mutableListOf<StatusSourceUriResolver>()) {
            val list = this
            ServiceLoader.load(StatusSourceUriResolver::class.java)
                .iterator()
                .forEach { list += it }
            this
        }.toImmutableList()

    suspend fun resolve(sourceUri: StatusSourceUri): StatusSource? {
        resolvers.forEach {
            val source = it.resolve(sourceUri)
            if (source != null) return source
        }
        return null
    }
}

interface StatusSourceUriResolver {

    suspend fun resolve(uri: StatusSourceUri): StatusSource?
}
