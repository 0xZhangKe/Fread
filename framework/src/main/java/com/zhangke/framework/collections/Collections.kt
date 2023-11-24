package com.zhangke.framework.collections

inline fun <T, R> Iterable<T>.mapFirst(transform: (T) -> R?): R {
    return mapFirstOrNull(transform) ?: throw NoSuchElementException()
//    return mapTo(ArrayList<R>(collectionSizeOrDefault(10)), transform)
}

inline fun <T, R> Iterable<T>.mapFirstOrNull(transform: (T) -> R?): R? {
    forEach {
        val v = transform(it)
        if (v != null) {
            return v
        }
    }
    return null
}

inline fun <T> Iterable<T>.container(predicate: (T) -> Boolean): Boolean {
    return firstOrNull(predicate) != null
}

inline fun <T> Iterable<T>.remove(predicate: (T) -> Boolean): List<T> {
    return filter { !predicate(it) }
}
