package com.zhangke.utopia.status.utils

import okhttp3.internal.toImmutableList
import java.util.*

object ImplementerFinder {

    val cachedImplementer = mutableMapOf<String, List<Any>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> findImplementer(): List<T> {
        val key = buildCacheKey<T>()
        cachedImplementer[key]?.let {
            return it as List<T>
        }
        val result = findImplementerInternal<T>()
        cachedImplementer[key] = result
        return result
    }

    inline fun <reified T> findImplementerInternal(): List<T> {
        return with(mutableListOf<T>()) {
            val list = this
            ServiceLoader.load(T::class.java)
                .iterator()
                .forEach { list += it }
            this
        }.toImmutableList()
    }

    inline fun <reified T> buildCacheKey(): String {
        return T::class.qualifiedName ?: throw IllegalArgumentException(
            "${T::class} is local or anonymous object."
        )
    }
}
