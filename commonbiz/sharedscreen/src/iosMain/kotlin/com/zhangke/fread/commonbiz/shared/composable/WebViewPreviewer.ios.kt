package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import platform.UIKit.UIColor
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.javaScriptEnabled
import platform.darwin.NSObject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun WebViewPreviewer(html: String, modifier: Modifier) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val fontColor = LocalContentColor.current
    UIKitView(
        factory = {
            WKWebView().apply {
                configuration.preferences.javaScriptEnabled = true
                backgroundColor = UIColor.colorWithRed(
                    fontColor.red.toDouble(),
                    fontColor.green.toDouble(),
                    fontColor.blue.toDouble(),
                    fontColor.alpha.toDouble(),
                )
                // TODO: fontSize
                setNavigationDelegate(object : NSObject(), WKNavigationDelegateProtocol {
                    override fun webView(
                        webView: WKWebView,
                        decidePolicyForNavigationAction: WKNavigationAction,
                        decisionHandler: (WKNavigationActionPolicy) -> Unit
                    ) {
                        val requestUrl = decidePolicyForNavigationAction.request.URL
                        if (requestUrl == null) {
                            decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                            return
                        }

                        val strRequestUrl = requestUrl.absoluteString
                        if (strRequestUrl == null) {
                            decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                            return
                        }

                        browserLauncher.launchWebTabInApp(strRequestUrl)
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                    }
                })
            }
        },
        update = {
            val finalHtml = warpBlogContentHtml(html, fontColor.toArgb())
            it.loadHTMLString(finalHtml, null)
        },
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative,
        ),
        onRelease = {
        },
        modifier = modifier,
    )
}
