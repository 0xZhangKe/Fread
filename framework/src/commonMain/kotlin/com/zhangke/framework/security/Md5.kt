package com.zhangke.framework.security

import io.ktor.utils.io.core.toByteArray
import okio.ByteString

object Md5 {

    fun md5(input: String): String {
        return ByteString.of(*input.toByteArray()).md5().hex()
    }
}