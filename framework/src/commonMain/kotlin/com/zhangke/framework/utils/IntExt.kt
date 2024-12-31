package com.zhangke.framework.utils

import com.ionspin.kotlin.bignum.decimal.toBigDecimal

fun Long.formatToHumanReadable(): String {
    return if (this >= 1_000_000) {
        (this / 1_000_000F).decimal(1)
            .removeSuffix(".0")
            .plus("M")
    } else if (this >= 1000) {
        (this / 1000F).decimal(1)
            .removeSuffix(".0")
            .plus("K")
    } else {
        this.toString()
    }
}

fun Int.formatToHumanReadable(): String {
    return this.toLong().formatToHumanReadable()
}

fun Float.decimal(digits: Int): String {
    return this.toBigDecimal()
        .scale(digits.toLong())
        .toStringExpanded()
}

fun Double.decimal(digits: Int): String {
    return this.toBigDecimal()
        .scale(digits.toLong())
        .toStringExpanded()
}
