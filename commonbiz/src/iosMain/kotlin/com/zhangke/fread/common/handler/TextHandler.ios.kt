package com.zhangke.fread.common.handler

import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

@ApplicationScope
actual class TextHandler @Inject constructor() {
    actual val packageName: String
        get() = NSBundle.mainBundle().bundleIdentifier().orEmpty()

    actual val versionName: String
        get() = NSBundle.mainBundle().infoDictionary()?.get("CFBundleShortVersionString") as? String ?: ""

    actual val versionCode: String
        get() = NSBundle.mainBundle().infoDictionary()?.get("CFBundleVersion") as? String ?: ""
}

@ActivityScope
actual class ActivityTextHandler @Inject constructor(
    private val textHandler: TextHandler,
) {

    actual val packageName: String
        get() = textHandler.packageName

    actual val versionName: String
        get() = textHandler.versionName
    actual val versionCode: String
        get() = textHandler.versionCode

    actual fun copyText(text: String) {
        val pasteboard = UIPasteboard.generalPasteboard()
        pasteboard.string = text
    }

    actual fun shareUrl(url: String, text: String) {
        val items = listOf(url, text)
        val activityViewController = UIActivityViewController(items, null)
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }

    actual fun openSendEmail() {
        val mailtoUrl = "mailto:"
        val url = NSURL.URLWithString(mailtoUrl)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    actual fun openAppMarket() {
        val appStoreUrl = "https://apps.apple.com/cn/app/id${textHandler.packageName}"
        val url = NSURL.URLWithString(appStoreUrl)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}