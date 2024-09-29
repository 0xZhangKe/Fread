package com.zhangke.fread.utils

import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Inject

@ActivityScope
actual class ActivityHelper @Inject constructor(
    // private val viewController: Lazy<UIViewController>,
) {
    actual fun goHome() {
        // viewController.value.dismissViewControllerAnimated(false, null)
    }
}
