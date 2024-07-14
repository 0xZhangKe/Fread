package com.zhangke.fread.common.browser

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.status.model.IdentityRole

object BrowserLauncher {

    fun launchWebTabInApp(
        context: Context,
        url: String,
        role: IdentityRole? = null,
        checkAppSupportPage: Boolean = true,
    ) {
        launchWebTabInApp(
            context = context,
            uri = Uri.parse(url),
            role = role,
            checkAppSupportPage = checkAppSupportPage,
        )
    }

    fun launchWebTabInApp(
        context: Context,
        uri: Uri,
        role: IdentityRole? = null,
        checkAppSupportPage: Boolean = true,
    ) {
        if (role != null && checkAppSupportPage) {
            BrowserBridgeDialogActivity.open(context, role, uri.toString())
            return
        }
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, uri)
    }

    fun launchBySystemBrowser(context: Context, url: String) {
        launchBySystemBrowser(context, Uri.parse(url))
    }

    fun launchBySystemBrowser(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun launchFreadLandingPage(context: Context) {
        launchWebTabInApp(context, AppCommonConfig.WEBSITE)
    }

    fun launchAuthorWebsite(context: Context) {
        launchWebTabInApp(context, AppCommonConfig.AUTHOR_WEBSITE)
    }
}
