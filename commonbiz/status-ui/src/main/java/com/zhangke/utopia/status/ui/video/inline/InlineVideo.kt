package com.zhangke.utopia.status.ui.video.inline

import android.net.Uri
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.zhangke.utopia.status.ui.utils.toMediaSource
import kotlinx.coroutines.delay
import kotlin.math.abs

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun InlineVideo(
    aspectRatio: Float?,
    coverImage: String?,
    playWhenReady: Boolean,
    style: InlineVideoPlayerStyle = InlineVideoPlayerDefault.defaultStyle,
    uri: Uri,
    onPlayManually: () -> Unit,
) {
    InlineVideoShell(
        aspectRatio = aspectRatio ?: style.defaultMediaAspect,
        style = style,
    ) {
        InlineVideoPlayer(
            uri = uri,
            coverImage = coverImage,
            playWhenReady = playWhenReady,
            onPlayManually = onPlayManually,
        )
    }
}

@Composable
private fun InlineVideoShell(
    aspectRatio: Float,
    style: InlineVideoPlayerStyle,
    content: @Composable () -> Unit,
) {
    val fixedAspectRatio = aspectRatio
        .coerceAtLeast(style.minAspect)
        .coerceAtMost(style.maxAspect)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(style.radius))
            .fillMaxWidth()
            .aspectRatio(fixedAspectRatio),
        content = {
            content()
        },
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun InlineVideoPlayer(
    uri: Uri,
    coverImage: String?,
    playWhenReady: Boolean,
    onPlayManually: () -> Unit,
) {
    val context = LocalContext.current
    val state = rememberInlineVideoState()
    val currentPlayWhenReady by remember(playWhenReady) {
        mutableStateOf(playWhenReady)
    }
    val playerListener = remember(uri) {
        object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                state.onPlaybackStateChanged(playbackState)
            }

            override fun onVolumeChanged(volume: Float) {
                super.onVolumeChanged(volume)
                state.onVolumeChanged(volume)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                state.onIsPlayingChanged(isPlaying)
            }
        }
    }
    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                addListener(playerListener)
                setMediaSource(uri.toMediaSource())
                prepare()
                seekTo(state.playerPosition)
                volume = state.playerVolume
                this.playWhenReady = false
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }
    LaunchedEffect(currentPlayWhenReady){
        exoPlayer.playWhenReady = currentPlayWhenReady
    }
    LaunchedEffect(uri) {
        while (true) {
            delay(100)
            state.updatePosition(exoPlayer.currentPosition)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = {
                SurfaceView(it).apply {
                    exoPlayer.setVideoSurfaceView(this)
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                }
            },
        )
        if (coverImage.isNullOrEmpty().not() && !currentPlayWhenReady) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = coverImage,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }
        InlineVideoControlPanel(
            playWhenReady = currentPlayWhenReady,
            playEnded = state.playbackEnded,
            mute = state.playerVolume <= 0F,
            onPlayClick = {
                val diff = abs(exoPlayer.duration - exoPlayer.currentPosition)
                if (diff < 100L) {
                    exoPlayer.seekTo(0L)
                }
                if (!state.playing) {
                    exoPlayer.play()
                    onPlayManually()
                }
            },
            onMuteClick = { mute ->
                if (mute) {
                    exoPlayer.volume = 0F
                } else {
                    exoPlayer.volume = 1F
                }
            },
        )
    }

    DisposableEffect(uri) {
        onDispose {
            state.updatePosition(exoPlayer.currentPosition)
            exoPlayer.removeListener(playerListener)
            exoPlayer.release()
        }
    }
}

@Composable
private fun InlineVideoControlPanel(
    playWhenReady: Boolean,
    playEnded: Boolean,
    mute: Boolean,
    onPlayClick: () -> Unit,
    onMuteClick: (mute: Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (!playWhenReady || playEnded) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.Center),
                onClick = onPlayClick,
            ) {
                Icon(
                    modifier = Modifier.size(42.dp),
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = "Play",
                    tint = Color.White,
                )
            }
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 6.dp),
            onClick = { onMuteClick(!mute) },
        ) {
            val icon = if (mute) {
                Icons.Default.VolumeOff
            } else {
                Icons.Default.VolumeUp
            }
            Icon(
                imageVector = icon,
                contentDescription = if (mute) "unmute" else "mute",
                tint = Color.White,
            )
        }
    }
}

data class InlineVideoPlayerStyle(
    val radius: Dp,
    val defaultMediaAspect: Float,
    val minAspect: Float,
    val maxAspect: Float,
)

object InlineVideoPlayerDefault {

    val defaultStyle = InlineVideoPlayerStyle(
        radius = 8.dp,
        defaultMediaAspect = 1.0F,
        maxAspect = 2.5F,
        minAspect = 0.7F,
    )
}
