package com.zhangke.fread.common.handler

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.zhangke.framework.utils.SystemPageUtils
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.framework.utils.startActivityCompat
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.utils.ShareHelper

actual class TextHandler (
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

    actual fun translateText(text: String) {
        if (text.isBlank()) return
        val intent = Intent(Intent.ACTION_PROCESS_TEXT).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_PROCESS_TEXT, text)
            putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
        }
        val chooser = Intent.createChooser(intent, "Translate")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivityCompat(chooser)
        } else {
            // Fall back to the share sheet so the user can pick a translation app.
            ShareHelper.shareUrl(context, "", text)
        }
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