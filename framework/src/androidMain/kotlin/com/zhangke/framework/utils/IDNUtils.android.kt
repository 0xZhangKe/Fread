package com.zhangke.framework.utils

import java.net.IDN

actual class IDNUtils {

    actual fun toASCII(input: String): String {
        return IDN.toASCII(input, IDN.ALLOW_UNASSIGNED)
    }
}
