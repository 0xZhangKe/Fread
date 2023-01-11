package com.zhangke.framework.collections


public inline fun <T, R> Iterable<T>.mapFirst(transform: (T) -> R?): R {
    return mapFirstOrNull(transform) ?: throw NoSuchElementException()
//    return mapTo(ArrayList<R>(collectionSizeOrDefault(10)), transform)
}

public inline fun <T, R> Iterable<T>.mapFirstOrNull(transform: (T) -> R?): R? {
    forEach {
        val v = transform(it)
        if (v != null) {
            return v
        }
    }
    return null
}