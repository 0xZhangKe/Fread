package com.zhangke.fread.common.utils

import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class ToastHelper @Inject constructor() {
    actual fun showToast(content: String) {
    }
}