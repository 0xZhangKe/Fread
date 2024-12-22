package com.zhangke.fread.screen

import androidx.compose.ui.window.ComposeUIViewController
import me.tatarka.inject.annotations.Inject
import platform.UIKit.UIViewController

typealias FreadViewController = () -> UIViewController

@Suppress("FunctionName")
@Inject
internal fun FreadViewController(
    iosFreadApp: IosFreadApp,
): UIViewController = ComposeUIViewController {
    iosFreadApp()
}
