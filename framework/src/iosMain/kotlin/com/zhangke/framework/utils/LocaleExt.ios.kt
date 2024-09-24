package com.zhangke.framework.utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getDefaultLanguage(): String {
    return NSLocale.currentLocale().languageCode
}
