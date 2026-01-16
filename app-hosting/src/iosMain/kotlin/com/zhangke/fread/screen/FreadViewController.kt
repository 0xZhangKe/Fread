package com.zhangke.fread.screen

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

typealias FreadViewController = () -> UIViewController

@Suppress("FunctionName")
internal fun FreadViewController(
    iosFreadApp: IosFreadApp,
): UIViewController = ComposeUIViewController {
    iosFreadApp()
}
