package com.zhangke.utopia.activitypub.app.internal.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.toast.toast
import com.zhangke.utopia.activitypub.app.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by ZhangKe on 2022/12/4.
 */
@AndroidEntryPoint
class ActivityPubOAuthRedirectActivity : AppCompatActivity() {

    @Inject
    lateinit var author: ActivityPubOAuthor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
