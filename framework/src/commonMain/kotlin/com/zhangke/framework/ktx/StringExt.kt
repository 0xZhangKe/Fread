package com.zhangke.framework.ktx

inline fun String?.ifNullOrEmpty(block: () -> String): String {
    if (this == null) return block()
    return ifEmpty(block)
}
