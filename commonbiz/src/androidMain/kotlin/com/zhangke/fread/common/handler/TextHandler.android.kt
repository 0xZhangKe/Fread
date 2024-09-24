package com.zhangke.fread.common.handler

import com.zhangke.framework.utils.SystemUtils
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.utils.ShareHelper
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class TextHandler @Inject constructor(
    private val context: ApplicationContext,
) {
    actual fun copyText(text: String) {
        SystemUtils.copyText(context, text)
    }

    actual fun shareUrl(url: String, text: String) {
        ShareHelper.shareUrl(context, url, text)
    }
}