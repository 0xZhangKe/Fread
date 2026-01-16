package com.zhangke.fread.common.utils

import android.content.Intent

typealias ActivityResultCallback = (resultCode: Int, Intent?) -> Unit

interface CallbackableActivity {

    fun registerCallback(requestCode: Int, callback: ActivityResultCallback)
}
