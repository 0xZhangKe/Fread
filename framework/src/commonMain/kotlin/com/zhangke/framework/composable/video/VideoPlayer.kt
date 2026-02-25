package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.github.kdroidfilter.composemediaplayer.InitialPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerError as KdVideoPlayerError
import io.github.kdroidfilter.composemediaplayer.VideoPlayerState as KdVideoPlayerState

@Stable
class VideoPlayerController internal constructor(
    val state: KdVideoPlayerState,
    initialContentScale: ContentScale,
) {

    private var rememberedUnmuteVolume by mutableFloatStateOf(1f)

    var contentScale by mutableStateOf(initialContentScale)
        private set

    val isPlaying: Boolean
        get() = state.isPlaying

    val isBuffering: Boolean
        get() = state.isLoading

    val isMuted: Boolean
        get() = state.volume <= 0f

    val currentTimeInSeconds: Float
        get() = state.currentTime.toFloat()

    val totalDurationInSeconds: Float
        get() = resolveDurationSeconds()

    val hasPlaybackEnded: Boolean
        get() {
            val duration = totalDurationInSeconds
            if (duration <= 0f) return false
            if (state.loop || state.isPlaying || state.isLoading) return false
            return currentTimeInSeconds >= (duration - 0.2f)
        }

    val lastError: KdVideoPlayerError?
        get() = state.error

    val positionText: String
        get() = state.positionText

    val durationText: String
        get() = state.durationText

    val progress: Float
        get() = state.sliderPos

    fun load(
        mediaUrl: String,
        autoPlay: Boolean = true,
    ) {
        val initialState = if (autoPlay) {
            InitialPlayerState.PLAY
        } else {
            InitialPlayerState.PAUSE
        }
        state.openUri(mediaUrl, initialState)
    }

    fun play() {
        if (hasPlaybackEnded) {
            state.seekTo(0F)
        }
        state.play()
    }

    fun pause() {
        state.pause()
    }

    fun stop() {
        state.stop()
    }

    fun togglePlayPause() {
        if (state.isPlaying) {
            state.pause()
        } else {
            state.play()
        }
    }

    fun mute() {
        val currentVolume = state.volume
        if (currentVolume > 0f) {
            rememberedUnmuteVolume = currentVolume
        }
        state.volume = 0f
    }

    fun unmute() {
        state.volume = rememberedUnmuteVolume.coerceIn(0.01f, 1f)
    }

    fun toggleMute() {
        if (isMuted) {
            unmute()
        } else {
            mute()
        }
    }

    fun setMuted(muted: Boolean) {
        if (muted) {
            mute()
        } else {
            unmute()
        }
    }

    fun setLooping(looping: Boolean) {
        state.loop = looping
    }

    fun setPlaybackSpeed(speed: Float) {
        state.playbackSpeed = speed.coerceIn(0.5f, 2f)
    }

    fun setVolume(level: Float) {
        val safeLevel = level.coerceIn(0f, 1f)
        if (safeLevel > 0f) {
            rememberedUnmuteVolume = safeLevel
        }
        state.volume = safeLevel
    }

    fun seekToProgress(progress: Float) {
        val target = progress.coerceIn(0f, 1000f)
        state.sliderPos = target
        state.userDragging = false
        state.seekTo(target)
    }

    fun seekTo(seconds: Float?) {
        if (seconds == null) return
        seekToSeconds(seconds)
    }

    fun seekToSeconds(seconds: Float) {
        val duration = totalDurationInSeconds
        if (duration <= 0f) return
        seekToProgress(seconds / duration * 1000f)
    }

    fun updateContentScale(contentScale: ContentScale) {
        this.contentScale = contentScale
    }

    fun toggleFullScreen() {
        state.toggleFullscreen()
    }

    fun clearError() {
        state.clearError()
    }

    private fun resolveDurationSeconds(): Float {
        val metadataDurationSeconds = state.metadata.duration
            ?.takeIf { it > 0L }
            ?.toFloat()
            ?.div(1000f)
        if (metadataDurationSeconds != null) return metadataDurationSeconds
        return parseDurationText(state.durationText)
    }
}

@Composable
fun rememberVideoPlayerController(
    mediaUrl: String,
    autoPlay: Boolean = true,
    initialMuted: Boolean = false,
    initialPlaybackSpeed: Float = 1f,
    initialContentScale: ContentScale = ContentScale.Crop,
    isLooping: Boolean = true,
    startTimeInSeconds: Float? = null,
): VideoPlayerController {
    val playerState = rememberVideoPlayerState()
    val controller = remember(playerState) {
        VideoPlayerController(
            state = playerState,
            initialContentScale = initialContentScale,
        )
    }

    LaunchedEffect(mediaUrl, autoPlay) {
        controller.load(
            mediaUrl = mediaUrl,
            autoPlay = autoPlay,
        )
    }

    LaunchedEffect(isLooping) {
        controller.setLooping(isLooping)
    }

    LaunchedEffect(initialPlaybackSpeed) {
        controller.setPlaybackSpeed(initialPlaybackSpeed)
    }

    LaunchedEffect(initialContentScale) {
        controller.updateContentScale(initialContentScale)
    }

    LaunchedEffect(initialMuted) {
        controller.setMuted(initialMuted)
    }

    LaunchedEffect(startTimeInSeconds) {
        startTimeInSeconds?.let(controller::seekToSeconds)
    }

    return controller
}

@Composable
fun VideoPlayer(
    mediaUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    initialMuted: Boolean = false,
    initialPlaybackSpeed: Float = 1f,
    initialContentScale: ContentScale = ContentScale.Crop,
    isLooping: Boolean = true,
    startTimeInSeconds: Float? = null,
    overlay: @Composable () -> Unit = {},
) {
    val controller = rememberVideoPlayerController(
        mediaUrl = mediaUrl,
        autoPlay = autoPlay,
        initialMuted = initialMuted,
        initialPlaybackSpeed = initialPlaybackSpeed,
        initialContentScale = initialContentScale,
        isLooping = isLooping,
        startTimeInSeconds = startTimeInSeconds,
    )
    VideoPlayer(
        controller = controller,
        modifier = modifier,
        overlay = overlay,
    )
}

@Composable
fun VideoPlayer(
    controller: VideoPlayerController,
    modifier: Modifier = Modifier,
    overlay: @Composable () -> Unit = {},
) {
    VideoPlayerSurface(
        playerState = controller.state,
        modifier = modifier,
        contentScale = controller.contentScale,
        overlay = overlay,
    )
}

private fun parseDurationText(durationText: String): Float {
    if (durationText.isBlank()) return 0f
    val parts = durationText.split(":")
    return when (parts.size) {
        2 -> {
            val minutes = parts[0].toFloatOrNull() ?: return 0f
            val seconds = parts[1].toFloatOrNull() ?: return 0f
            minutes * 60f + seconds
        }

        3 -> {
            val hours = parts[0].toFloatOrNull() ?: return 0f
            val minutes = parts[1].toFloatOrNull() ?: return 0f
            val seconds = parts[2].toFloatOrNull() ?: return 0f
            hours * 3600f + minutes * 60f + seconds
        }

        else -> 0f
    }
}
