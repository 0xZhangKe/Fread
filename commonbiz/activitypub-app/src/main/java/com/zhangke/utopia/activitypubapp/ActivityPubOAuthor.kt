package com.zhangke.utopia.activitypubapp

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Created by ZhangKe on 2022/12/4.
 */
object ActivityPubOAuthor {

    private val oauthCode: SharedFlow<String> = MutableSharedFlow(1)

    fun perform(activity: Activity) {
        val oauthUrl = "https://m.cmx.im/oauth/authorize" +
                "?response_type=code" +
                "&client_id=KHGSFM7oZY2_ZhaQRo25DfBRNwERZy7_iqZ_HjA5Sp8" +
                "&redirect_uri=utopia://oauth.utopia" +
                "&scope=read+write+follow+push"

        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.launchUrl(activity, Uri.parse(oauthUrl))
    }
}