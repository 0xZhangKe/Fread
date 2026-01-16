package com.zhangke.fread.utils

import android.content.Context
import android.content.Intent
import com.zhangke.framework.utils.startActivityCompat
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Inject

@ActivityScope
actual class ActivityHelper @Inject constructor(
    private val context: Context,
) {
    actual fun goHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_HOME)
        }
        context.startActivityCompat(intent)
    }
}
