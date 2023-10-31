package com.zhangke.utopia.status.ui.video.full

import android.net.Uri
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.utopia.status.ui.utils.toMediaSource
import kotlinx.coroutines.delay

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun FullScreenVideoPlayer(
    uri: Uri,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ToolbarTokens.ContainerHeight)
                .padding(horizontal = ToolbarTokens.TopAppBarHorizontalPadding)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { onBackClick() },
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                    "back",
                    tint = Color.White,
                )
            }
        }

        var volume by rememberSaveable {
            mutableFloatStateOf(0F)
        }
        var playerPosition by rememberSaveable {
            mutableLongStateOf(0L)
        }

        var playWhenReady by remember {
            mutableStateOf(true)
        }

        val playerListener = remember(uri) {
            object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
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
                    seekTo(playerPosition)
                    this.playWhenReady = false
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                    repeatMode = Player.REPEAT_MODE_OFF
                }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = {
                    SurfaceView(it).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        )
                        exoPlayer.setVideoSurfaceView(this)
                    }
                },
            )
        }
        LaunchedEffect(volume) {
            exoPlayer.volume = volume
        }
        LaunchedEffect(playWhenReady) {
            exoPlayer.playWhenReady = playWhenReady
        }
        LaunchedEffect(uri) {
            while (true) {
                delay(500)
                playerPosition = exoPlayer.currentPosition
            }
        }
    }
}

@Composable
private fun FullScreenVideoPlayerPanel(
    modifier: Modifier = Modifier,
    playWhenReady: Boolean,
    playEnded: Boolean,
    mute: Boolean,
    progress: Float,
    duration: Long,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
    onMuteClick: (mute: Boolean) -> Unit,
    onProgressChangeRequest: (progress: Float) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
    ) {
        val (controlBtnRef, progressRef, timeRef) = createRefs()
        PlayPauseIconButton(
            modifier = Modifier.constrainAs(controlBtnRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, 10.dp)
            },
            playWhenReady = playWhenReady,
            onPauseClick = onPauseClick,
            onPlayClick = onPlayClick,
        )
        PlayerProgress(
            modifier = Modifier.constrainAs(progressRef) {
                top.linkTo(parent.top)
                bottom.linkTo(timeRef.top)
                start.linkTo(controlBtnRef.end, 4.dp)
                end.linkTo(parent.end, 12.dp)
            },
            progress = progress,
            onProgressChangeRequest = onProgressChangeRequest,
        )
        Text(
            modifier = Modifier.constrainAs(timeRef) {
                top.linkTo(progressRef.bottom, 2.dp)
                bottom.linkTo(parent.bottom)
                end.linkTo(progressRef.end)
            },
            fontSize = 12.sp,
            text = buildProgressTimeDesc(progress, duration),
            color = Color.White,
        )
    }
}

private fun buildProgressTimeDesc(progress: Float, duration: Long): String {
    return ""
}

@Composable
private fun PlayPauseIconButton(
    modifier: Modifier,
    playWhenReady: Boolean,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = {
            if (playWhenReady) {
                onPauseClick()
            } else {
                onPlayClick()
            }
        },
    ) {
        val icon = if (playWhenReady) {
            Icons.Default.Pause
        } else {
            Icons.Default.PlayArrow
        }
        Icon(
            painter = rememberVectorPainter(icon),
            contentDescription = if (playWhenReady) "pause" else "play",
        )
    }
}

@Composable
private fun PlayerProgress(
    modifier: Modifier,
    progress: Float,
    onProgressChangeRequest: (progress: Float) -> Unit,
) {
    Slider(
        modifier = modifier,
        value = progress,
        onValueChange = onProgressChangeRequest,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            activeTickColor = Color.White,
        )
    )
}
