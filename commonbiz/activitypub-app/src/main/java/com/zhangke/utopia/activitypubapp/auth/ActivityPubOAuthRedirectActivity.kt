package com.zhangke.utopia.activitypubapp.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.toast.toast
import com.zhangke.utopia.activitypubapp.R
import dagger.hilt.android.AndroidEntryPoint
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
        if (code.isNullOrEmpty()) {
            toast(getString(R.string.activity_pub_login_exception))
        } else {
            lifecycleScope.launch {
                author.onOauthSuccess(code)
            }
        }
        finish()
    }
}
