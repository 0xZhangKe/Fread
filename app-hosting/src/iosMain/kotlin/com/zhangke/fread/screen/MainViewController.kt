package com.zhangke.fread.screen

import androidx.compose.ui.window.ComposeUIViewController
import com.zhangke.fread.di.IosActivityComponent

@Suppress("FunctionName")
fun MainViewController(
    component: IosActivityComponent,
) = ComposeUIViewController {
    IosFreadApp(
        activityComponent = component,
    )
}
