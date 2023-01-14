package com.zhangke.utopia.activitypubapp.oauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhangke.framework.toast.toast
import com.zhangke.utopia.activitypubapp.R

/**
 * Created by ZhangKe on 2022/12/4.
 */
class ActivityPubOAuthRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = intent.data?.getQueryParameter("code")
        if (code.isNullOrEmpty()) {
            toast(getString(R.string.activity_pub_login_exception))
        } else {
            ActivityPubOAuthor.onOauthSuccess(code)
        }

        finish()
    }
}