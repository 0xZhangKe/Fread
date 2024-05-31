package com.zhangke.framework.ktx

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed

fun <T> Collection<T>.isSingle(): Boolean = size == 1

fun <T> List<T>.second(): T {
    return this[1]
}

fun <T> List<T>.third(): T {
    return this[2]
}

fun <T> List<T>.fourth(): T {
    return this[3]
}

fun List<Float>.averageDropFirst(count: Int): Double {
    var sum = 0.0
    for (index in count .. lastIndex) {
        sum += get(index)
    }
    return sum / count
}

fun Iterable<Dp>.sum(): Dp {
    var sum: Dp = 0.dp
    for (element in this) {
        sum += element
    }
    return sum
}

fun <T> List<T>.distinctByKey(getKey: (index: Int, item: T) -> String): List<T> {
    if (this.size < 2) return this
    val keySet = mutableSetOf<String>()
    val newList = mutableListOf<T>()
    this.fastForEachIndexed { index, item ->
        val key = getKey(index, item)
        if (!keySet.contains(key)) {
            keySet += key
            newList += item
        }
    }
    return newList
}
