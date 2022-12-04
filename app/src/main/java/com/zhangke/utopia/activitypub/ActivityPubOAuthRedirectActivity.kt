package com.zhangke.utopia.activitypub

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Created by ZhangKe on 2022/12/4.
 */
class ActivityPubOAuthRedirectActivity : AppCompatActivity() {

    companion object {

        private const val ARG_URL = "arg_url"

        fun open(activity: Activity, oauthUrl: String) {
//            val intent = Intent(activity, ActivityPubOAuthActivity::class.java).apply {
//                putExtra(ARG_URL, oauthUrl)
//            }
//            activity.startActivity(intent)

            val packageName = "com.android.chrome"
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(activity, Uri.parse(oauthUrl))

//            try {
//                val i = Intent("android.intent.action.MAIN")
//                i.component = ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.CustomTabActivity")
//                i.addCategory("android.intent.category.LAUNCHER")
//                i.data = Uri.parse(oauthUrl)
//                activity.startActivity(i)
//            } catch (e: ActivityNotFoundException) {
//                // Chrome is not installed
//                val i = Intent(Intent.ACTION_VIEW, Uri.parse(oauthUrl))
//                activity.startActivity(i)
//            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra(ARG_URL)!!

        val webView = buildWebView()
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContentView(webView)
        webView.loadUrl(url)
    }

    private fun buildWebView(): WebView {
        val webView = WebView(this)
        webView.webChromeClient = WebChromeClient()
        return webView
    }
}