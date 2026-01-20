package com.zhangke.fread.utils

import platform.UIKit.UIViewController

actual class ActivityHelper(
    private val viewController: Lazy<UIViewController>,
) {
    actual fun goHome() {
        viewController.value.dismissViewControllerAnimated(false, null)
    }
}
