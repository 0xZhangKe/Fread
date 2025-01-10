package com.zhangke.framework.utils

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalForeignApi::class)
fun CValue<CMTime>.toDuration(): Duration {
    return CMTimeGetSeconds(this).takeUnless { it.isNaN() }
        ?.toDuration(DurationUnit.SECONDS)
        ?: Duration.ZERO
}