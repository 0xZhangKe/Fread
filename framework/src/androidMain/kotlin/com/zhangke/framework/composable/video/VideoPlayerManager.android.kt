package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberVideoPlayerManager(): VideoPlayerManager {
    val context = LocalContext.current
    return remember(context) {
        val factory = ExoVideoPlayerController.Factory(context)
        VideoPlayerManager(factory)
    }
}
