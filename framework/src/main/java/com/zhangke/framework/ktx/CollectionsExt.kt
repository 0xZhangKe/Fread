package com.zhangke.framework.ktx

fun <T> Collection<T>.isSingle(): Boolean = size == 1

fun <T> List<T>.second(): T {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return this[1]
}
