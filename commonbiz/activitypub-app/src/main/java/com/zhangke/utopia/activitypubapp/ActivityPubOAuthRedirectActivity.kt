package com.zhangke.utopia.activitypubapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by ZhangKe on 2022/12/4.
 */
class ActivityPubOAuthRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = intent.data?.getQueryParameter("code")

    }
}