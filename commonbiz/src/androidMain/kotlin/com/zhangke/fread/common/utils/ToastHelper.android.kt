package com.zhangke.fread.common.utils

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Inject

@ActivityScope
actual class ToastHelper @Inject constructor(
    private val activity: Activity,
) {
    actual fun showToast(content: String) {
        val toast = Toast.makeText(
            activity,
            content,
            Toast.LENGTH_SHORT,
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}