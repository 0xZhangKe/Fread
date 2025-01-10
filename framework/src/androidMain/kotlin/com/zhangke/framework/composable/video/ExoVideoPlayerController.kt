package com.zhangke.framework.composable.video

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toAndroidUri
import com.zhangke.framework.utils.toMediaSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ExoVideoPlayerController(
    private var exoPlayer: ExoPlayer,
) : VideoPlayerController {

    private val _volumeFlow = MutableStateFlow(0f)
    override val volumeFlow: Flow<Float> = _volumeFlow.asStateFlow()

    private val _hasVideoEndedFlow = MutableStateFlow<Boolean>(false)
    override val hasVideoEndedFlow: Flow<Boolean> = _hasVideoEndedFlow.asStateFlow()

    private val _isPlayingFlow = MutableStateFlow(false)
    override val isPlayingFlow: Flow<Boolean> = _isPlayingFlow.asStateFlow()

    private val _isLoadingFlow = MutableStateFlow(false)
    override val isLoadingFlow: Flow<Boolean> = _isLoadingFlow.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    override val errorFlow: Flow<String> = _errorFlow.asSharedFlow()

    override val duration: Duration
        get() = exoPlayer.duration.milliseconds

    override val currentPosition: Duration
        get() = exoPlayer.currentPosition.milliseconds

    private val playerListener = object : Player.Listener {
        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
            _volumeFlow.value = volume
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _hasVideoEndedFlow.value = playbackState == Player.STATE_ENDED
            if (playbackState == Player.STATE_READY) {
                _isLoadingFlow.value = false
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _isPlayingFlow.value = isPlaying
            if (isPlaying) {
                _isLoadingFlow.value = false
            }
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            super.onIsLoadingChanged(isLoading)
            _isLoadingFlow.value = isLoading
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            _errorFlow.tryEmit(error.message ?: "Unknown error")
        }
    }

    @OptIn(UnstableApi::class)
    override fun prepare(
        state: VideoState,
        playWhenReady: Boolean,
    ) {
        exoPlayer.addListener(playerListener)
        exoPlayer.volume = state.playerVolume
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
        exoPlayer.prepare()
        exoPlayer.seekTo(state.targetSeekTo)
    }

    override fun play() {
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun release() {
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume
    }

    @OptIn(UnstableApi::class)
    @Composable
    override fun Content(
        aspectRatio: VideoAspectRatio,
        modifier: Modifier,
    ) {
        AndroidView(
            modifier = modifier,
            factory = {
                PlayerView(it).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    player = exoPlayer
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    resizeMode = when (aspectRatio) {
                        VideoAspectRatio.ScaleToFit -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                        VideoAspectRatio.ScaleToFill -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        VideoAspectRatio.FillStretch -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                }
            },
        )
    }

    class Factory(
        private val context: Context,
    ) : VideoPlayerController.Factory {
        @UnstableApi
        override fun create(uri: PlatformUri): VideoPlayerController {
            return ExoVideoPlayerController(
                exoPlayer = ExoPlayer.Builder(context)
                    .build().apply {
                        setMediaSource(uri.toAndroidUri().toMediaSource())
                    },
            )
        }
    }
}

