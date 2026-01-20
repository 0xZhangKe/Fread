package com.zhangke.fread.common.browser

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.zhangke.fread.common.di.ApplicationContext
import kotlinx.coroutines.CompletableDeferred

actual class OAuthHandler (
    private val context: ApplicationContext,
) {

    private var oauthCodeCompletable: CompletableDeferred<String>? = null

    actual suspend fun startOAuth(url: String): String {
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, url.toUri())

        val code = CompletableDeferred<String>().also { oauthCodeCompletable = it }.await()
        return code
    }

    fun onOauthSuccess(code: String) {
        oauthCodeCompletable?.let {
            it.complete(code)
            oauthCodeCompletable = null
        }
    }
}