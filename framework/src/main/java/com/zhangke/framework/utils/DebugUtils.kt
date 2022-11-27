package com.zhangke.framework.utils

import com.zhangke.framework.BuildConfig

inline fun ifDebugging(block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}