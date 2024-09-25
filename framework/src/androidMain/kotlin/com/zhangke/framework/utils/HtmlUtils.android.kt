package com.zhangke.framework.utils

import androidx.core.text.HtmlCompat

actual fun String.htmlToText(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}
