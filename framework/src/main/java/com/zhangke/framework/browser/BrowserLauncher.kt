package com.zhangke.framework.browser

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object BrowserLauncher {

    fun launchWebTabInApp(context: Context, url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun launchBySystemBrowser(context: Context, url: String) {
        launchBySystemBrowser(context, Uri.parse(url))
    }

    fun launchBySystemBrowser(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
