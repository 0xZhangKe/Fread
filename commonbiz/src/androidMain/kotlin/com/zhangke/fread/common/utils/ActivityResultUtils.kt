package com.zhangke.fread.common.utils

import android.app.Activity
import android.content.Intent

typealias ActivityResultCallback = (resultCode: Int, Intent?) -> Unit

fun Activity.registerActivityResultCallback(
    requestCode: Int,
    callback: ActivityResultCallback,
) {
    if (this is CallbackableActivity) {
        this.registerCallback(requestCode, callback)
    }
}

interface CallbackableActivity {

    fun registerCallback(requestCode: Int, callback: ActivityResultCallback)
}
