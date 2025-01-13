package com.zhangke.fread.activitypub.app.internal.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.toast.toast
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_login_exception
import com.zhangke.fread.common.commonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ZhangKe on 2022/12/4.
 */
class ActivityPubOAuthRedirectActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = intent.data?.getQueryParameter("code")
        lifecycleScope.launch {
            if (code.isNullOrEmpty()) {
                toast(org.jetbrains.compose.resources.getString(Res.string.activity_pub_login_exception))
                delay(2000)
            } else {
                commonComponent.oauthHandler.onOauthSuccess(code)
            }
            finish()
        }
    }
}
