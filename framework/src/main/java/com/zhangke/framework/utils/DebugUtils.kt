package com.zhangke.framework.utils

var appDebuggable = false
    private set

fun initDebuggable(debug: Boolean) {
    appDebuggable = debug
}

inline fun ifDebugging(block: () -> Unit) {
    if (appDebuggable) {
        block()
    }
}
