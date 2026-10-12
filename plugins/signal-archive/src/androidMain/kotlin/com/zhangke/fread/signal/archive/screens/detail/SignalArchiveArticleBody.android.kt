package com.zhangke.fread.signal.archive.screens.detail

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal actual fun SignalArchiveArticleBody(
    html: String,
    modifier: Modifier,
    onLinkClick: (String) -> Unit,
) {
    val linkClick = rememberUpdatedState(onLinkClick)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                        val url = request.url
                        if (url.scheme == "http" || url.scheme == "https") linkClick.value(url.toString())
                        return true
                    }
                }
            }
        },
        update = { view ->
            if (view.tag != html) {
                view.tag = html
                view.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }
        },
        onRelease = { view -> view.stopLoading(); view.destroy() },
    )
}
