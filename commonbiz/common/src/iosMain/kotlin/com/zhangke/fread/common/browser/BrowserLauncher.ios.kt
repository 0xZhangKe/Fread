package com.zhangke.fread.common.browser

import com.eygraber.uri.toNSURL
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

@ActivityScope
class IosActivityBrowserLauncher @Inject constructor(
    private val viewController: Lazy<UIViewController>,
    private val application: UIApplication,
) : ActivityBrowserLauncher {

    override fun launchBySystemBrowser(uri: PlatformUri) {
        application.openURL(uri.toNSURL()!!)
    }

    override fun launchWebTabInApp(
        uri: PlatformUri,
        locator: PlatformLocator?,
        checkAppSupportPage: Boolean,
    ) {
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
