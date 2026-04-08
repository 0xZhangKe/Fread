package com.zhangke.framework.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun <T : NavKey> NavBackStack<T>.popIfNotRoot(): Boolean {
    if (size <= 1) return false
    removeAt(lastIndex)
    return true
}

fun <T : NavKey> NavBackStack<T>.replaceTopOrAdd(key: T) {
    if (isEmpty()) {
        add(key)
        return
    }
    this[lastIndex] = key
}
