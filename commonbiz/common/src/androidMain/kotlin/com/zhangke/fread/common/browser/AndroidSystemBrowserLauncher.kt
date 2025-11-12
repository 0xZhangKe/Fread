package com.zhangke.fread.common.browser

import android.app.Activity
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toAndroidUri
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Inject

@ActivityScope
class AndroidSystemBrowserLauncher @Inject constructor(private val activity: Activity) : SystemBrowserLauncher {

    override fun launchBySystemBrowser(uri: PlatformUri) {
        val intent = Intent(Intent.ACTION_VIEW, uri.toAndroidUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            activity.startActivity(intent)
        } catch (_: Throwable) {
            // ignore
        }
    }

    override fun launchWebTabInApp(uri: PlatformUri) {
        try {
            CustomTabsIntent.Builder().build().launchUrl(activity, uri.toAndroidUri())
        } catch (_: Throwable) {
            // ignore
        }
    }
}
