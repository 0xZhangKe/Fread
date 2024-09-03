package com.zhangke.framework.utils

import com.ionspin.kotlin.bignum.decimal.toBigDecimal

fun Int.formatAsCount(): String {
    require(this >= 0) { "Size must larger than 0." }

    val byte = this.toDouble()
    val kb = byte / 1024.0
    val mb = byte / 1024.0 / 1024.0
    val gb = byte / 1024.0 / 1024.0 / 1024.0
    // val tb = byte / 1024.0 / 1024.0 / 1024.0 / 1024.0

    return when {
        // tb >= 1 -> "${tb.decimal(1)} TB"
        gb >= 1 -> "${gb.decimal(1)}GB"
        mb >= 1 -> "${mb.decimal(1)}MB"
        kb >= 1 -> "${kb.decimal(1)}KB"
        else -> "${byte.decimal(1)}B"
    }
}

private fun Double.decimal(digits: Long): String {
    return this.toBigDecimal()
        .scale(digits)
        .toStringExpanded()
}