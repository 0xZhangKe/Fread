package com.zhangke.utopia.status_provider

import okhttp3.internal.toImmutableList
import java.util.*

class ImplementerFinder {

    inline fun <reified T> findImplementer(): List<T> {
        return with(mutableListOf<T>()) {
            val list = this
            ServiceLoader.load(T::class.java)
                .iterator()
                .forEach { list += it }
            this
        }.toImmutableList()
    }
}