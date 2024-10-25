package com.zhangke.framework.utils

inline fun <T> T.maybe(predication: Boolean, block: (T) -> T): T {
    return if (predication) block(this) else this
}
