package com.zhangke.fread.activitypub.app.internal.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.toast.toast
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.di.activityPubComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ZhangKe on 2022/12/4.
 */
class ActivityPubOAuthRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val author = activityPubComponent.author

        val code = intent.data?.getQueryParameter("code")
        lifecycleScope.launch {
            if (code.isNullOrEmpty()) {
                toast(getString(R.string.activity_pub_login_exception))
                delay(2000)
            } else {
                author.onOauthSuccess(code)
            }
            finish()
        }
    }
}
