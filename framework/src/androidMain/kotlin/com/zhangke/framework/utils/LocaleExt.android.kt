package com.zhangke.framework.utils

import java.util.Locale

actual fun getDefaultLanguage(): String {
    return Locale.getDefault().language
}
