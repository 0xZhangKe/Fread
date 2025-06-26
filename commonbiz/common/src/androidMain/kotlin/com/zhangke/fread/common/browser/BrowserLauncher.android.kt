package com.zhangke.fread.common.browser

import android.app.Activity
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toAndroidUri
import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

@ActivityScope
class AndroidActivityBrowserLauncher @Inject constructor(
    private val activity: Activity,
) : ActivityBrowserLauncher {

    override fun launchBySystemBrowser(uri: PlatformUri) {
        val intent = Intent(Intent.ACTION_VIEW, uri.toAndroidUri())
        try {
            activity.startActivity(intent)
        } catch (_: Throwable) {
            // ignore
        }
    }

    override fun launchWebTabInApp(
        uri: PlatformUri,
        locator: PlatformLocator?,
        checkAppSupportPage: Boolean,
    ) {
        if (locator != null && checkAppSupportPage) {
            BrowserBridgeDialogActivity.open(activity, locator, uri.toString())
            return
        }
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        try {
            customTabsIntent.launchUrl(activity, uri.toAndroidUri())
        } catch (_: Throwable) {
            // ignore
        }
    }
}
