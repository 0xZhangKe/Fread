package com.zhangke.framework.utils

import android.content.Context
import android.util.TypedValue

context(context: Context)
fun Int.dpToPx(): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
        .toInt()
}
