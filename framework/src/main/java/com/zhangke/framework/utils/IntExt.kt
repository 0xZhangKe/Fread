package com.zhangke.framework.utils

fun Int.formatAsCount(): String {
    val count = this
    return if (count >= 1_000_000) {
        "%.1f".format(count / 1_000_000F)
            .removeSuffix(".0")
            .plus("M")
    } else if (count >= 1000) {
        "%.1f".format(count / 1000F)
            .removeSuffix(".0")
            .plus("K")
    } else {
        count.toString()
    }
}
