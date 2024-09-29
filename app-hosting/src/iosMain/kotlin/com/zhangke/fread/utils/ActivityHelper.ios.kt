package com.zhangke.fread.utils

import me.tatarka.inject.annotations.Inject
import platform.UIKit.UIViewController

actual class ActivityHelper @Inject constructor(
    private val viewController: Lazy<UIViewController>,
) {
    actual fun goHome() {
        viewController.value.dismissViewControllerAnimated(false, null)
    }
}
