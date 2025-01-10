package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberVideoPlayerManager(): VideoPlayerManager {
    return remember {
        val factory = AvVideoPlayer.Factory()
        VideoPlayerManager(factory)
    }
}