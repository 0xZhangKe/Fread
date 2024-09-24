package com.zhangke.fread.common.handler

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.zhangke.framework.utils.SystemPageUtils
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.utils.ShareHelper
import me.tatarka.inject.annotations.Inject

actual class TextHandler @Inject constructor(
    private val context: ApplicationContext,
) {
    actual val packageName: String
        get() = context.packageName

    actual val versionName: String
        get() = SystemUtils.getAppVersionName(context)

    actual val versionCode: String
        get() = SystemUtils.getAppVersionCode(context)
}

@ActivityScope
actual class ActivityTextHandler @Inject constructor(
    private val textHandler: TextHandler,
    private val activity: Activity,
) {

    actual val packageName: String
        get() = textHandler.packageName

    actual val versionName: String
        get() = textHandler.versionName

    actual val versionCode: String
        get() = textHandler.versionCode

    actual fun copyText(text: String) {
        SystemUtils.copyText(activity, text)
    }

    actual fun shareUrl(url: String, text: String) {
        ShareHelper.shareUrl(activity, url, text)
    }

    actual fun openSendEmail() {
        SystemUtils.copyText(activity, AppCommonConfig.AUTHOR_EMAIL)
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, AppCommonConfig.AUTHOR_EMAIL)
        activity.startActivity(intent)
    }

    actual fun openAppMarket() {
        SystemPageUtils.openAppMarket(activity)
    }
}