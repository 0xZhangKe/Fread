package com.zhangke.fread.signal.archive.screens.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSObject
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationActionPolicyAllow
import platform.WebKit.WKNavigationActionPolicyCancel
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKNavigationTypeLinkActivated
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal actual fun SignalArchiveArticleBody(
    html: String,
    modifier: Modifier,
    onLinkClick: (String) -> Unit,
) {
    val linkClick = rememberUpdatedState(onLinkClick)
    val loadedHtml = remember { arrayOfNulls<String>(1) }
    // WKWebView retains its navigation delegate weakly.
    val delegate = remember {
        object : NSObject(), WKNavigationDelegateProtocol {
            override fun webView(
                webView: WKWebView,
                decidePolicyForNavigationAction: WKNavigationAction,
                decisionHandler: (WKNavigationActionPolicy) -> Unit,
            ) {
                if (decidePolicyForNavigationAction.navigationType == WKNavigationTypeLinkActivated) {
                    val url = decidePolicyForNavigationAction.request.URL
                    if (url?.scheme == "http" || url?.scheme == "https") {
                        url?.absoluteString?.let { linkClick.value(it) }
                    }
                    decisionHandler(WKNavigationActionPolicyCancel)
                } else {
                    decisionHandler(WKNavigationActionPolicyAllow)
                }
            }
        }
    }
    UIKitView(
        modifier = modifier,
        factory = {
            val configuration = WKWebViewConfiguration()
            configuration.preferences.javaScriptEnabled = false
            WKWebView(frame = CGRectZero.readValue(), configuration = configuration).apply {
                navigationDelegate = delegate
            }
        },
        update = { view ->
            if (loadedHtml[0] != html) {
                loadedHtml[0] = html
                view.loadHTMLString(html, baseURL = null)
            }
        },
        onRelease = { view ->
            view.stopLoading()
            view.navigationDelegate = null
            loadedHtml[0] = null
        },
    )
}
