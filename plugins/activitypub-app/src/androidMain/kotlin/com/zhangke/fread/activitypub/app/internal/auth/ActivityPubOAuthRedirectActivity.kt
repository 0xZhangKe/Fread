package com.zhangke.fread.activitypub.app.internal.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.toast.toast
import com.zhangke.fread.common.browser.OAuthHandler
import com.zhangke.fread.localization.LocalizedString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Created by ZhangKe on 2022/12/4.
 */
class ActivityPubOAuthRedirectActivity : ComponentActivity() {

    private val oauthHandler by inject<OAuthHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = intent.data?.getQueryParameter("code")
        lifecycleScope.launch {
            if (code.isNullOrEmpty()) {
                toast(org.jetbrains.compose.resources.getString(LocalizedString.activity_pub_login_exception))
                delay(2000)
            } else {
                oauthHandler.onOauthSuccess(code)
            }
            finish()
        }
    }
}
