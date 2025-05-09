package com.zhangke.fread.common.utils

import java.util.UUID

actual class RandomIdGenerator {

    actual fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}
