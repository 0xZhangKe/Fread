package com.zhangke.framework.composable.video

import android.util.Log
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize

actual abstract class PlatformVideoPlayerListener {

    actual abstract fun onVideoSizeChanged(aspectRatio: Float)
    actual abstract fun onPlayerError(error: String?)
    actual abstract fun onPlaybackEndChanged(ended: Boolean)
    actual abstract fun onVolumeChanged(volume: Float)
    actual abstract fun onIsPlayingChanged(isPlaying: Boolean)

    val exoPlayerListener = object : Player.Listener {

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            this@PlatformVideoPlayerListener.onVideoSizeChanged(
                videoSize.pixelWidthHeightRatio,
            )
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            Log.d("PlayerManager", "onPlaybackStateChanged:$playbackState")
            this@PlatformVideoPlayerListener.onPlaybackEndChanged(
                playbackState == Player.STATE_ENDED,
            )
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            this@PlatformVideoPlayerListener.onPlayerError(
                "Play error: ${error.errorCodeName},${error.errorCode}",
            )
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            super.onPlayerErrorChanged(error)
            this@PlatformVideoPlayerListener.onPlayerError(
                if (error == null) {
                    null
                } else {
                    "Play error: ${error.errorCodeName}, ${error.errorCode}"
                }
            )
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            Log.d("PlayerManager", "onIsPlayingChanged:$isPlaying")
            this@PlatformVideoPlayerListener.onIsPlayingChanged(isPlaying)
        }

        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
            this@PlatformVideoPlayerListener.onVolumeChanged(volume)
        }
    }
}