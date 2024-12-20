package com.zhangke.fread.common.utils

import kotlinx.datetime.Clock

actual class RandomIdGenerator {

    actual fun generateId(): String {
        // TODO get device info for generate id
        return Clock.System.now().toString()
    }
}
