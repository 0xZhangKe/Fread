package com.zhangke.fread.commonbiz.shared.composable

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.zhangke.framework.utils.dpToPx
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher


@Composable
actual fun WebViewPreviewer(
    html: String,
    modifier: Modifier,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val density = LocalDensity.current
    val fontColor = LocalContentColor.current.toArgb()
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
                        browserLauncher.launchWebTabInApp(request.url.toPlatformUri())
                        return true
                    }
                }
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
        },
        update = {
            val finalHtml = warpBlogContentHtml(html, fontColor)
            it.loadDataWithBaseURL("", finalHtml, "text/html", "UTF-8", null)
        },
    )
}

