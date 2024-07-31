package com.zhangke.framework.composable.video

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.zhangke.framework.utils.toMediaSource
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    uri: Uri,
    playWhenReady: Boolean,
    state: VideoState = rememberVideoPlayerState(),
    useController: Boolean = false,
) {
    val context = LocalContext.current
    var playErrorInfo: String? by remember {
        mutableStateOf(null)
    }
    var aspectRatio by remember {
        mutableFloatStateOf(1.778F)
    }
    val playerListener = remember(uri) {
        object : Player.Listener {

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                state.updateDuration(player.duration)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.d("PlayerManager", "onPlaybackStateChanged:$playbackState")
                state.onPlaybackStateChanged(playbackState)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                playErrorInfo = "Play error: ${error.errorCodeName},${error.errorCode}"
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
                playErrorInfo = if (error == null) {
                    null
                } else {
                    "Play error: ${error.errorCodeName}, ${error.errorCode}"
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                super.onVideoSizeChanged(videoSize)
                aspectRatio = videoSize.pixelWidthHeightRatio
            }

            override fun onVolumeChanged(volume: Float) {
                super.onVolumeChanged(volume)
                state.onVolumeChanged(volume)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                Log.d("PlayerManager", "onIsPlayingChanged:$isPlaying")
                state.onIsPlayingChanged(isPlaying)
            }
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val playerManager = LocalExoPlayerManager.current
    val exoPlayer = remember(uri) {
        playerManager.obtainPlayer(context, uri, lifecycle).apply {
            addListener(playerListener)
            this.playWhenReady = true
            volume = state.playerVolume
            repeatMode = Player.REPEAT_MODE_OFF
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            setMediaSource(uri.toMediaSource())
            prepare()
            seekTo(state.playerPosition)
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(aspectRatio),
            factory = {
                PlayerView(it).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            update = {
                it.player = exoPlayer
                it.useController = useController
            },
        )
        if (playErrorInfo.isNullOrBlank().not()) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                text = playErrorInfo.orEmpty(),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
    LaunchedEffect(uri, playWhenReady) {
        exoPlayer.playWhenReady = playWhenReady
    }
    LaunchedEffect(uri) {
        while (true) {
            delay(100)
            state.updatePosition(exoPlayer.currentPosition)
        }
    }
    LaunchedEffect(state.playerVolume) {
        if (exoPlayer.volume == state.playerVolume) return@LaunchedEffect
        exoPlayer.volume = state.playerVolume
    }
    LaunchedEffect(state.playing) {
        if (exoPlayer.isPlaying == state.playing) return@LaunchedEffect
        if (state.playing) {
            val diff = abs(exoPlayer.duration - exoPlayer.currentPosition)
            if (diff < 100L) {
                exoPlayer.seekTo(0L)
            }
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }
    DisposableEffect(uri) {
        onDispose {
            Log.d("PlayerManager", "onDispose")
            state.updatePosition(exoPlayer.currentPosition)
            exoPlayer.removeListener(playerListener)
            playerManager.recyclePlayer(uri)
        }
    }
}
