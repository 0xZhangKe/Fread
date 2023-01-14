package com.zhangke.framework.toast

import android.widget.Toast
import com.zhangke.framework.utils.appContext

fun toast(
    message: String?,
    length: Int = Toast.LENGTH_SHORT,
) {
    if (message.isNullOrEmpty()) return
    Toast.makeText(appContext, message, length).show()
}