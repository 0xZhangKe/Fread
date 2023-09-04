package com.zhangke.framework.utils

import android.graphics.drawable.Drawable

fun Drawable.aspectRatio(): Float {
    return intrinsicWidth.toFloat() / intrinsicHeight.toFloat()
}
