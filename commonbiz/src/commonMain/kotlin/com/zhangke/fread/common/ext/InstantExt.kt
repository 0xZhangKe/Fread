package com.zhangke.fread.common.ext

import kotlinx.datetime.Clock

fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}