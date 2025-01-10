package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.utils.PlatformUri
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface VideoPlayerController {

    val volumeFlow: Flow<Float>
    val hasVideoEndedFlow: Flow<Boolean>
    val isPlayingFlow: Flow<Boolean>
    val isLoadingFlow: Flow<Boolean>
    val errorFlow: Flow<String>

    val duration: Duration
    val currentPosition: Duration

    fun prepare(state: VideoState, playWhenReady: Boolean)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(position: Long)
    fun setVolume(volume: Float)

    @Composable
    fun Content(
        aspectRatio: VideoAspectRatio,
        modifier: Modifier,
    )

    interface Factory {
        fun create(uri: PlatformUri): VideoPlayerController
    }
}
