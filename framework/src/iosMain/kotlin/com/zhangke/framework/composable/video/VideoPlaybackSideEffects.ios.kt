package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable

@Composable
actual fun VideoPlaybackSideEffects(controller: VideoPlayerController) {
    // No-op on iOS: AVPlayer handles audio session + idle timer on its own.
}
