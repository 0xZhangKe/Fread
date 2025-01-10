package com.zhangke.framework.composable.video

actual abstract class PlatformVideoPlayerListener {
    actual abstract fun onVideoSizeChanged(aspectRatio: Float)
    actual abstract fun onPlayerError(error: String?)
    actual abstract fun onPlaybackEndChanged(ended: Boolean)
    actual abstract fun onVolumeChanged(volume: Float)
    actual abstract fun onIsPlayingChanged(isPlaying: Boolean)
}