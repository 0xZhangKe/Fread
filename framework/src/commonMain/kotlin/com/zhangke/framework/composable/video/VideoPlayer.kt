package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import chaintech.videoplayer.host.DrmConfig as ChaintechDrmConfig
import chaintech.videoplayer.host.MediaPlayerError as ChaintechMediaPlayerError
import chaintech.videoplayer.host.MediaPlayerEvent as ChaintechMediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.PlayerSpeed as ChaintechPlayerSpeed
import chaintech.videoplayer.model.ScreenResize as ChaintechScreenResize
import chaintech.videoplayer.model.VideoPlayerConfig as ChaintechVideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable as ChaintechVideoPlayerComposable

@Stable
class VideoPlayerController internal constructor(
    internal val host: MediaPlayerHost,
    autoPlay: Boolean,
    initialMuted: Boolean,
) {

    var isPlaying by mutableStateOf(autoPlay)
        private set
    var isMuted by mutableStateOf(initialMuted)
        private set
    var isBuffering by mutableStateOf(true)
        private set
    var isFullScreen by mutableStateOf(false)
        private set
    var isInPictureInPicture by mutableStateOf(false)
        private set
    var hasPlaybackEnded by mutableStateOf(false)
        private set
    var currentTimeInSeconds by mutableStateOf(0f)
        private set
    var totalDurationInSeconds by mutableStateOf(0f)
        private set
    var lastError by mutableStateOf<VideoPlayerError?>(null)
        private set

    internal fun bindCallbacks(
        onEvent: ((VideoPlayerEvent) -> Unit)?,
        onError: ((VideoPlayerError) -> Unit)?,
    ) {
        host.onEvent = { event ->
            val mappedEvent = event.toVideoPlayerEvent()
            updateState(mappedEvent)
            onEvent?.invoke(mappedEvent)
        }
        host.onError = { error ->
            val mappedError = error.toVideoPlayerError()
            lastError = mappedError
            onError?.invoke(mappedError)
        }
    }

    internal fun clearCallbacks() {
        host.onEvent = null
        host.onError = null
    }

    fun load(
        mediaUrl: String,
        headers: Map<String, String>? = null,
        drmConfig: VideoDrmConfig? = null,
    ) {
        hasPlaybackEnded = false
        lastError = null
        host.loadUrl(
            mediaUrl = mediaUrl,
            headers = headers,
            drmConfig = drmConfig.toChaintechDrmConfigOrNull(),
        )
    }

    fun play() {
        hasPlaybackEnded = false
        isPlaying = true
        host.play()
    }

    fun pause() {
        isPlaying = false
        host.pause()
    }

    fun togglePlayPause() {
        isPlaying = !isPlaying
        host.togglePlayPause()
    }

    fun mute() {
        isMuted = true
        host.mute()
    }

    fun unmute() {
        isMuted = false
        host.unmute()
    }

    fun toggleMute() {
        isMuted = !isMuted
        host.toggleMuteUnmute()
    }

    fun setSpeed(speed: VideoPlayerSpeed) {
        host.setSpeed(speed.toChaintechPlayerSpeed())
    }

    fun seekTo(seconds: Float?) {
        hasPlaybackEnded = false
        host.seekTo(seconds)
    }

    fun setLooping(looping: Boolean) {
        host.setLooping(looping)
    }

    fun setVolume(level: Float) {
        isMuted = level <= 0f
        host.setVolume(level)
    }

    fun setResizeMode(mode: VideoResizeMode) {
        host.setVideoFitMode(mode.toChaintechResizeMode())
    }

    fun setFullScreen(fullScreen: Boolean) {
        isFullScreen = fullScreen
        host.setFullScreen(fullScreen)
    }

    fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        host.toggleFullScreen()
    }

    private fun updateState(event: VideoPlayerEvent) {
        when (event) {
            is VideoPlayerEvent.MuteChanged -> isMuted = event.isMuted
            is VideoPlayerEvent.PlayingChanged -> isPlaying = event.isPlaying
            is VideoPlayerEvent.BufferingChanged -> isBuffering = event.isBuffering
            is VideoPlayerEvent.CurrentTimeChanged -> currentTimeInSeconds =
                event.currentTimeInSeconds

            is VideoPlayerEvent.DurationChanged -> totalDurationInSeconds =
                event.totalDurationInSeconds

            is VideoPlayerEvent.FullScreenChanged -> isFullScreen = event.isFullScreen
            is VideoPlayerEvent.PictureInPictureChanged -> isInPictureInPicture =
                event.isInPictureInPicture

            VideoPlayerEvent.PlaybackEnded -> hasPlaybackEnded = true
        }
    }
}

@Composable
fun rememberVideoPlayerController(
    mediaUrl: String,
    autoPlay: Boolean = true,
    initialMuted: Boolean = false,
    initialSpeed: VideoPlayerSpeed = VideoPlayerSpeed.X1,
    initialResizeMode: VideoResizeMode = VideoResizeMode.Fill,
    isLooping: Boolean = true,
    startTimeInSeconds: Float? = null,
    headers: Map<String, String>? = null,
    drmConfig: VideoDrmConfig? = null,
    onEvent: ((VideoPlayerEvent) -> Unit)? = null,
    onError: ((VideoPlayerError) -> Unit)? = null,
): VideoPlayerController {
    val latestOnEvent by rememberUpdatedState(newValue = onEvent)
    val latestOnError by rememberUpdatedState(newValue = onError)
    val controller = remember {
        VideoPlayerController(
            host = MediaPlayerHost(
                mediaUrl = "",
                autoPlay = autoPlay,
                isMuted = initialMuted,
                initialSpeed = initialSpeed.toChaintechPlayerSpeed(),
                initialVideoFitMode = initialResizeMode.toChaintechResizeMode(),
                isLooping = isLooping,
                startTimeInSeconds = startTimeInSeconds,
                headers = null,
                drmConfig = null,
            ),
            autoPlay = autoPlay,
            initialMuted = initialMuted,
        )
    }
    SideEffect {
        controller.bindCallbacks(
            onEvent = latestOnEvent,
            onError = latestOnError,
        )
    }
    LaunchedEffect(mediaUrl, headers, drmConfig) {
        controller.load(
            mediaUrl = mediaUrl,
            headers = headers,
            drmConfig = drmConfig,
        )
    }
    DisposableEffect(controller) {
        onDispose {
            controller.clearCallbacks()
        }
    }
    return controller
}

@Composable
fun VideoPlayer(
    mediaUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    initialMuted: Boolean = false,
    initialSpeed: VideoPlayerSpeed = VideoPlayerSpeed.X1,
    initialResizeMode: VideoResizeMode = VideoResizeMode.Fill,
    isLooping: Boolean = true,
    startTimeInSeconds: Float? = null,
    headers: Map<String, String>? = null,
    drmConfig: VideoDrmConfig? = null,
    onEvent: ((VideoPlayerEvent) -> Unit)? = null,
    onError: ((VideoPlayerError) -> Unit)? = null,
    options: VideoPlayerOptions = VideoPlayerOptions(),
) {
    val controller = rememberVideoPlayerController(
        mediaUrl = mediaUrl,
        autoPlay = autoPlay,
        initialMuted = initialMuted,
        initialSpeed = initialSpeed,
        initialResizeMode = initialResizeMode,
        isLooping = isLooping,
        startTimeInSeconds = startTimeInSeconds,
        headers = headers,
        drmConfig = drmConfig,
        onEvent = onEvent,
        onError = onError,
    )
    VideoPlayer(
        controller = controller,
        modifier = modifier,
        options = options,
    )
}

@Composable
fun VideoPlayer(
    controller: VideoPlayerController,
    modifier: Modifier = Modifier,
    options: VideoPlayerOptions = VideoPlayerOptions(),
) {
    ChaintechVideoPlayerComposable(
        modifier = modifier,
        playerHost = controller.host,
        playerConfig = options.toChaintechVideoPlayerConfig(),
    )
}

enum class VideoPlayerSpeed {
    X0_5, X1, X1_5, X2
}

enum class VideoResizeMode {
    Fit, Fill
}

data class VideoPlayerOptions(
    val showControls: Boolean = true,
    val allowPauseResume: Boolean = true,
    val showSeekBar: Boolean = true,
    val showDuration: Boolean = true,
    val allowMute: Boolean = true,
    val allowFullScreen: Boolean = true,
    val autoHideControls: Boolean = true,
    val controlHideIntervalSeconds: Int = 3,
    val allowPictureInPicture: Boolean = true,
)

data class VideoDrmConfig(
    val keyId: String,
    val key: String,
)

sealed interface VideoPlayerEvent {
    data class MuteChanged(val isMuted: Boolean) : VideoPlayerEvent
    data class PlayingChanged(val isPlaying: Boolean) : VideoPlayerEvent
    data class BufferingChanged(val isBuffering: Boolean) : VideoPlayerEvent
    data class CurrentTimeChanged(val currentTimeInSeconds: Float) : VideoPlayerEvent
    data class DurationChanged(val totalDurationInSeconds: Float) : VideoPlayerEvent
    data class FullScreenChanged(val isFullScreen: Boolean) : VideoPlayerEvent
    data class PictureInPictureChanged(val isInPictureInPicture: Boolean) : VideoPlayerEvent
    data object PlaybackEnded : VideoPlayerEvent
}

sealed interface VideoPlayerError {
    data object VlcNotFound : VideoPlayerError
    data class Initialization(val details: String) : VideoPlayerError
    data class Playback(val details: String) : VideoPlayerError
    data class Resource(val details: String) : VideoPlayerError
}

private fun VideoPlayerSpeed.toChaintechPlayerSpeed(): ChaintechPlayerSpeed {
    return when (this) {
        VideoPlayerSpeed.X0_5 -> ChaintechPlayerSpeed.X0_5
        VideoPlayerSpeed.X1 -> ChaintechPlayerSpeed.X1
        VideoPlayerSpeed.X1_5 -> ChaintechPlayerSpeed.X1_5
        VideoPlayerSpeed.X2 -> ChaintechPlayerSpeed.X2
    }
}

private fun VideoResizeMode.toChaintechResizeMode(): ChaintechScreenResize {
    return when (this) {
        VideoResizeMode.Fit -> ChaintechScreenResize.FIT
        VideoResizeMode.Fill -> ChaintechScreenResize.FILL
    }
}

private fun VideoDrmConfig?.toChaintechDrmConfigOrNull(): ChaintechDrmConfig? {
    return this?.let {
        ChaintechDrmConfig(
            keyId = keyId,
            key = key,
        )
    }
}

private fun VideoPlayerOptions.toChaintechVideoPlayerConfig(): ChaintechVideoPlayerConfig {
    return ChaintechVideoPlayerConfig(
        showControls = showControls,
        isPauseResumeEnabled = allowPauseResume,
        isSeekBarVisible = showSeekBar,
        isDurationVisible = showDuration,
        isMuteControlEnabled = allowMute,
        isFullScreenEnabled = allowFullScreen,
        isAutoHideControlEnabled = autoHideControls,
        controlHideIntervalSeconds = controlHideIntervalSeconds,
        enablePIPControl = allowPictureInPicture,
        showControlsOverride = showControls,
    )
}

private fun ChaintechMediaPlayerEvent.toVideoPlayerEvent(): VideoPlayerEvent {
    return when (this) {
        is ChaintechMediaPlayerEvent.MuteChange ->
            VideoPlayerEvent.MuteChanged(isMuted = isMuted)

        is ChaintechMediaPlayerEvent.PauseChange ->
            VideoPlayerEvent.PlayingChanged(isPlaying = !isPaused)

        is ChaintechMediaPlayerEvent.BufferChange ->
            VideoPlayerEvent.BufferingChanged(isBuffering = isBuffering)

        is ChaintechMediaPlayerEvent.CurrentTimeChange ->
            VideoPlayerEvent.CurrentTimeChanged(currentTimeInSeconds = currentTime)

        is ChaintechMediaPlayerEvent.TotalTimeChange ->
            VideoPlayerEvent.DurationChanged(totalDurationInSeconds = totalTime)

        is ChaintechMediaPlayerEvent.FullScreenChange ->
            VideoPlayerEvent.FullScreenChanged(isFullScreen = isFullScreen)

        is ChaintechMediaPlayerEvent.PIPChange ->
            VideoPlayerEvent.PictureInPictureChanged(isInPictureInPicture = isPip)

        ChaintechMediaPlayerEvent.MediaEnd -> VideoPlayerEvent.PlaybackEnded
    }
}

private fun ChaintechMediaPlayerError.toVideoPlayerError(): VideoPlayerError {
    return when (this) {
        ChaintechMediaPlayerError.VlcNotFound -> VideoPlayerError.VlcNotFound
        is ChaintechMediaPlayerError.InitializationError ->
            VideoPlayerError.Initialization(details = details)

        is ChaintechMediaPlayerError.PlaybackError ->
            VideoPlayerError.Playback(details = details)

        is ChaintechMediaPlayerError.ResourceError ->
            VideoPlayerError.Resource(details = details)
    }
}
