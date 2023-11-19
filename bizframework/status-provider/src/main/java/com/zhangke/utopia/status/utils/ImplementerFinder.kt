package com.zhangke.utopia.status.utils

import java.util.ServiceLoader

inline fun <reified T> findImplementer(): T {
    val list = findImplementers<T>()
    if (list.size != 1)
        throw IllegalStateException("${T::class.qualifiedName} has multiple implementers")
    return list.first()
}

inline fun <reified T> findImplementers(): List<T> {
    return ServiceLoader.load(T::class.java, T::class.java.classLoader)
        .iterator()
        .asSequence()
        .toList()
}
