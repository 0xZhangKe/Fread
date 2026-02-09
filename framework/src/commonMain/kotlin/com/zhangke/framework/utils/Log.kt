package com.zhangke.framework.utils

import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter

object Log {

    val log = Logger(
        loggerConfigInit(platformLogWriter()),
        "Fread",
    )

    inline fun d(tag: String, message: () -> String) {
        log.d(tag = tag, message = message)
    }

    inline fun i(tag: String, message: () -> String) {
        log.i(tag = tag, message = message)
    }
}