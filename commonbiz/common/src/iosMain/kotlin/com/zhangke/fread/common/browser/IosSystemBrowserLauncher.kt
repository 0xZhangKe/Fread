package com.zhangke.fread.common.browser

import com.eygraber.uri.toNSURL
import com.zhangke.framework.utils.PlatformUri
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class IosSystemBrowserLauncher(
    private val viewController: Lazy<UIViewController>,
    private val application: UIApplication,
) : SystemBrowserLauncher {

    override fun launchBySystemBrowser(uri: PlatformUri) {
        application.openURL(uri.toNSURL()!!)
    }

    override fun launchWebTabInApp(uri: PlatformUri) {
        try {
            val safari = SFSafariViewController(uri.toNSURL()!!)
            // safari.modalPresentationStyle = UIModalPresentationPageSheet
            viewController.value.presentViewController(safari, animated = true, completion = null)
        } catch (e: Exception) {
            e.printStackTrace()
            launchBySystemBrowser(uri)
        }
    }
}
