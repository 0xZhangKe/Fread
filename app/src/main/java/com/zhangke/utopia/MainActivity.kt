package com.zhangke.utopia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.zhangke.utopia.activitypubapp.OauthActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv).setOnClickListener {
            OauthActivity.open(this)
        }
    }
}