package com.zhangke.framework.composable.video

expect abstract class PlatformVideoPlayerListener() {
    abstract fun onVideoSizeChanged(aspectRatio: Float)
    abstract fun onPlayerError(error: String?)
    abstract fun onPlaybackEndChanged(ended: Boolean)
    abstract fun onVolumeChanged(volume: Float)
    abstract fun onIsPlayingChanged(isPlaying: Boolean)
}
