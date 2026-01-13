package com.zhangke.fread.common.handler

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.zhangke.framework.utils.SystemPageUtils
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.framework.utils.startActivityCompat
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.utils.ShareHelper
import me.tatarka.inject.annotations.Inject

actual class TextHandler @Inject constructor(
    private val context: Context,
) {
    actual val packageName: String
        get() = context.packageName

    actual val versionName: String
        get() = SystemUtils.getAppVersionName(context)

    actual val versionCode: String
        get() = SystemUtils.getAppVersionCode(context)

    actual fun copyText(text: String) {
        SystemUtils.copyText(context, text)
    }

    actual fun shareUrl(url: String, text: String) {
        ShareHelper.shareUrl(context, url, text)
    }

    actual fun openSendEmail() {
        SystemUtils.copyText(context, AppCommonConfig.AUTHOR_EMAIL)
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = "mailto:".toUri()
        intent.putExtra(Intent.EXTRA_EMAIL, AppCommonConfig.AUTHOR_EMAIL)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivityCompat(intent)
        }
    }

    actual fun openAppMarket() {
        SystemPageUtils.openAppMarket(context)
    }
}
