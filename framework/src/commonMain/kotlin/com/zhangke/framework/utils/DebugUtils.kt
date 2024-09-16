package com.zhangke.framework.utils

import com.zhangke.framework.toast.toast

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

fun throwInDebug(message: String?, throwable: Throwable? = null) {
    if (appDebuggable) {
        toast("Non-fatal error! ${message ?: throwable?.message}")
        if (throwable != null) throw throwable
        throw ThrowInDebugException(message)
    } else {
        // TODO report to server
    }
}

class ThrowInDebugException(message: String?) : RuntimeException(message)