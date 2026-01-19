package com.zhangke.fread.common.browser

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.extractActivity
import com.zhangke.framework.utils.startActivityCompat
import com.zhangke.framework.utils.toAndroidUri

class AndroidSystemBrowserLauncher(
    private val context: Context,
) : SystemBrowserLauncher {

    override fun launchBySystemBrowser(uri: PlatformUri) {
        val intent = Intent(Intent.ACTION_VIEW, uri.toAndroidUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivityCompat(intent)
        } catch (_: Throwable) {
            // ignore
        }
    }

    override fun launchWebTabInApp(uri: PlatformUri) {
        try {
            CustomTabsIntent.Builder().build()
                .launchUrl(context.extractActivity() ?: context, uri.toAndroidUri())
        } catch (_: Throwable) {
            // ignore
        }
    }
}
