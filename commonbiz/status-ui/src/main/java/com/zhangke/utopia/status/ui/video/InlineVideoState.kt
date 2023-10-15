package com.zhangke.utopia.status.ui.video

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
fun rememberInlineVideoState(
    initialPlayerProgress: Long = 0L,
): InlineVideoState {
    return rememberSaveable(saver = InlineVideoState.Saver) {
        InlineVideoState(
            initialPlayerProgress = initialPlayerProgress,
        )
    }
}

@Stable
class InlineVideoState(
    initialPlayerProgress: Long,
) {
    var playing by mutableStateOf(false)
        private set
    var playWhenReady by mutableStateOf(true)
        private set
    var playbackEnded by mutableStateOf(false)
        private set
    var playerVolume by mutableFloatStateOf(0F)
        private set
    var playerPosition by mutableLongStateOf(initialPlayerProgress)
        private set

    var renderedFirstFrame by mutableStateOf(false)

    fun onPlaybackStateChanged(playbackState: Int) {
        playbackEnded = playbackState == Player.STATE_ENDED
    }

    fun onPlayWhenReadyChanged(whenReady: Boolean) {
        playWhenReady = whenReady
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

    companion object {

        val Saver: Saver<InlineVideoState, *> = Saver(
            save = { state ->
                arrayOf(
                    state.playerPosition,
                )
            },
            restore = { array ->
                InlineVideoState(
                    array[0] as Long,
                )
            },
        )
    }
}
