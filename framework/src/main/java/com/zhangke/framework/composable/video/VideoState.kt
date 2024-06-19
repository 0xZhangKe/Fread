package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.media3.common.Player

@Composable
fun rememberVideoPlayerState(
    initialPlayerProgress: Long = 0L,
): VideoState {
    return rememberSaveable(saver = VideoState.Saver) {
        VideoState(
            initialPlayerProgress = initialPlayerProgress,
        )
    }
}

@Stable
class VideoState(
    initialPlayerProgress: Long,
) {

    internal var playing by mutableStateOf(false)
        private set
    var playbackEnded by mutableStateOf(false)
        private set
    var playerVolume by mutableFloatStateOf(0F)
        private set
    internal var playerPosition by mutableLongStateOf(initialPlayerProgress)
        private set

    fun onPlaybackStateChanged(playbackState: Int) {
        playbackEnded = playbackState == Player.STATE_ENDED
    }

    fun onVolumeChanged(volume: Float) {
        playerVolume = volume
    }

    fun onIsPlayingChanged(isPlaying: Boolean) {
        playing = isPlaying
    }

    fun updatePosition(progress: Long) {
        playerPosition = progress
    }

    fun mute() {
        playerVolume = 0F
    }

    fun unmute() {
        playerVolume = 1F
    }

    fun seekTo(position: Long) {
        playerPosition = position
    }

    fun play() {
        playing = true
    }

    fun stop() {
        playing = false
    }

    companion object {

        val Saver: Saver<VideoState, *> = Saver(
            save = { state ->
                arrayOf(
                    state.playerPosition,
                )
            },
            restore = { array ->
                VideoState(
                    array[0] as Long,
                )
            },
        )
    }
}
