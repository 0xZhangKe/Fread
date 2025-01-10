package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.utils.PlatformUri

interface PlatformVideoPlayer {
    fun prepare(state: VideoState, playWhenReady: Boolean)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(position: Long)

    var volume: Float
    val duration: Long
    val currentPosition: Long
    val isPlaying: Boolean

    @Composable
    fun Content(modifier: Modifier)

    fun addListener(listener: PlatformVideoPlayerListener)
    fun removeListener(listener: PlatformVideoPlayerListener)

    interface Factory {
        fun create(uri: PlatformUri): PlatformVideoPlayer
    }
}
