package com.zhangke.fread.utils

import android.content.Context
import android.content.Intent
import com.zhangke.framework.utils.startActivityCompat

actual class ActivityHelper(private val context: Context) {
    actual fun goHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_HOME)
        }
        context.startActivityCompat(intent)
    }
}
