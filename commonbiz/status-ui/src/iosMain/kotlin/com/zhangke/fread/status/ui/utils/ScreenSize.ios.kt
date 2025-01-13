package com.zhangke.fread.status.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@ReadOnlyComposable
@Composable
actual fun getScreenWidth(): Dp {
    return with(LocalDensity.current) {
        // TODO: This is not the correct way to get the screen width
        UIScreen.mainScreen().bounds().useContents {
            size.width.toInt().dp
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@ReadOnlyComposable
@Composable
actual fun getScreenHeight(): Dp {
    return with(LocalDensity.current) {
        // TODO: This is not the correct way to get the screen width
        UIScreen.mainScreen().bounds().useContents {
            size.height.toInt().dp
        }
    }
}