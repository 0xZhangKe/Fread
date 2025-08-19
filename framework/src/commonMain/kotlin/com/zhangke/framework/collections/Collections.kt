package com.zhangke.framework.collections

inline fun <T, R> Iterable<T>.mapFirst(transform: (T) -> R?): R {
    return mapFirstOrNull(transform) ?: throw NoSuchElementException()
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

fun <T> Iterable<T>.removeIndex(index: Int): List<T> {
    return filterIndexed { i, _ -> i != index }
}

fun <T> List<T>.updateItem(item: T, predicate: (T) -> T): List<T> {
    return map {
        if (it == item) {
            predicate(it)
        } else {
            it
        }
    }
}

inline fun <T> Iterable<T>.updateIndex(index: Int, predicate: (T) -> T): List<T> {
    return mapIndexed { i, t ->
        if (i == index) {
            predicate(t)
        } else {
            t
        }
    }
}

fun <T> MutableIterable<T>.removeFirstOrNull(block: (T) -> Boolean): T? {
    val iterator = iterator()
    while (iterator.hasNext()){
        val item = iterator.next()
        if (block(item)) {
            iterator.remove()
            return item
        }
    }
    return null
}

fun <T> Collection<T>.getOrNull(block: (T) -> Boolean): T? {
    for (item in this) {
        if (block(item)) {
            return item
        }
    }
    return null
}
