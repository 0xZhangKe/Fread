package com.zhangke.fread.common.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

object SystemUtils {

    fun openAppStore(packageName: String) {
        val appStoreUrl = "https://apps.apple.com/cn/app/id${packageName}"
        val url = NSURL.URLWithString(appStoreUrl)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}
