package com.zhangke.fread.common.browser

import android.app.Activity
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toAndroidUri
import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class AndroidBrowserLauncher @Inject constructor(
    private val context: ApplicationContext,
) : BrowserLauncher {

    override fun launchBySystemBrowser(uri: PlatformUri) {
        val intent = Intent(Intent.ACTION_VIEW, uri.toAndroidUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


}

@ActivityScope
class AndroidActivityBrowserLauncher @Inject constructor(
    private val activity: Activity,
) : ActivityBrowserLauncher {

    override fun launchBySystemBrowser(uri: PlatformUri) {
        val intent = Intent(Intent.ACTION_VIEW, uri.toAndroidUri())
        activity.startActivity(intent)
    }

    override fun launchWebTabInApp(
        uri: PlatformUri,
        role: IdentityRole?,
        checkAppSupportPage: Boolean,
    ) {
        if (role != null && checkAppSupportPage) {
            BrowserBridgeDialogActivity.open(activity, role, uri.toString())
            return
        }
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.launchUrl(activity, uri.toAndroidUri())
    }
}
