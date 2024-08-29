package com.zhangke.framework.utils

import kotlin.math.abs

fun Float.equalsExactly(target: Float): Boolean {
    return abs(target - this) <= 0.000001F
}
