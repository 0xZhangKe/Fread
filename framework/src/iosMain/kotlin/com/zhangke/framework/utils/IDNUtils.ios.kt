package com.zhangke.framework.utils

actual class IDNUtils {

    actual fun toASCII(input: String): String {
        throw UnsupportedOperationException("IDNUtils is not supported on this platform.")
    }
}
