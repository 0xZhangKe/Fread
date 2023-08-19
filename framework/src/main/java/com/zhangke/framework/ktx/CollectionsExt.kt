package com.zhangke.framework.ktx

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
