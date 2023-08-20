package com.zhangke.framework.ktx

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
