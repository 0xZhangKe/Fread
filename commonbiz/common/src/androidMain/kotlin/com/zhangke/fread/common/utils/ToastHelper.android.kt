package com.zhangke.fread.common.utils

import android.view.Gravity
import android.widget.Toast
import com.zhangke.fread.common.di.ApplicationContext

actual class ToastHelper(
    private val context: ApplicationContext,
) {
    actual fun showToast(content: String) {
        val toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}
