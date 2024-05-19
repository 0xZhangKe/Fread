package com.zhangke.framework.composable.web

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.zhangke.framework.browser.BrowserLauncher
import com.zhangke.framework.utils.dpToPx

@Composable
fun WebViewPreviewer(
    html: String,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    AndroidView(
        modifier = modifier,
        factory = {
            WebView(it).apply {
                this.setBackgroundColor(Color.Transparent.toArgb())
                settings.defaultFontSize = 16.dp.dpToPx(density).toInt()
                @SuppressLint("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest,
                    ): Boolean {
                        BrowserLauncher.launchBySystemBrowser(context, request.url)
                        return true
                    }
                }
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
        },
        update = {
            it.loadDataWithBaseURL("", html, "text/html", "UTF-8", null)
        },
    )
}
