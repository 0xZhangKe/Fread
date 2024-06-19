package com.zhangke.fread.status.ui.video.full

import android.net.Uri
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.video.LocalFullscreenExoPlayerManager
import com.zhangke.framework.utils.toMediaSource
import com.zhangke.fread.status.ui.video.VideoDurationFormatter
import kotlinx.coroutines.delay

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun FullScreenVideoPlayer(
    modifier: Modifier = Modifier,
    uri: Uri,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var mute by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(color = Color.Black)
    ) {
        var playerPosition by rememberSaveable {
            mutableLongStateOf(0L)
        }

        val lifecycle = LocalLifecycleOwner.current
        val playerManager = LocalFullscreenExoPlayerManager.current

        val exoPlayer = remember(uri) {
            playerManager.obtainPlayer(context, uri, lifecycle.lifecycle).apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                this.playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ALL
                setMediaSource(uri.toMediaSource())
                prepare()
                seekTo(playerPosition)
            }
        }
        LaunchedEffect(uri) {
            while (true) {
                delay(60)
                playerPosition = exoPlayer.currentPosition
            }
        }
        var panelVisible by remember {
            mutableStateOf(true)
        }
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClick {
                    panelVisible = !panelVisible
                },
            factory = {
                PlayerView(it).apply {
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                }
            },
            update = {
                it.player = exoPlayer
            },
        )

        LaunchedEffect(mute) {
            if (mute) {
                exoPlayer.volume = 0F
            } else {
                exoPlayer.volume = 1F
            }
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = panelVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                FullScreenVideoPlayerPanel(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    playing = exoPlayer.isPlaying,
                    mute = mute,
                    onPlayClick = {
                        if (exoPlayer.isPlaying) return@FullScreenVideoPlayerPanel
                        exoPlayer.play()
                    },
                    onPauseClick = {
                        if (!exoPlayer.isPlaying) return@FullScreenVideoPlayerPanel
                        exoPlayer.pause()
                    },
                    playerPosition = playerPosition,
                    duration = exoPlayer.duration,
                    onPositionChangeRequest = {
                        exoPlayer.seekTo(it)
                    },
                    onMuteClick = {
                        mute = true
                    },
                    onUnmuteClick = {
                        mute = false
                    },
                )
                FullScreenPlayerToolBar(onBackClick)
            }
        }
    }
}

@Composable
private fun FullScreenPlayerToolBar(
    onBackClick: () -> Unit,
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
}

@Composable
private fun FullScreenVideoPlayerPanel(
    modifier: Modifier = Modifier,
    playing: Boolean,
    playerPosition: Long,
    duration: Long,
    mute: Boolean,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPositionChangeRequest: (position: Long) -> Unit,
    onMuteClick: () -> Unit,
    onUnmuteClick: () -> Unit,
) {
    val progress = playerPosition / duration.toFloat()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0x05000000), Color(0xF9000000))))
            .padding(bottom = 16.dp),
    ) {
        PlayerProgress(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            progress = progress,
            onProgressChange = { progress ->
                onPositionChangeRequest((duration * progress).toLong())
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayPauseIconButton(
                modifier = Modifier.padding(start = 4.dp),
                playing = playing,
                onPauseClick = onPauseClick,
                onPlayClick = onPlayClick,
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                modifier = Modifier.padding(end = 4.dp),
                fontSize = 12.sp,
                text = VideoDurationFormatter.formatVideoProgressDesc(playerPosition, duration),
                color = Color.White,
            )
            IconButton(
                modifier = Modifier
                    .padding(end = 8.dp),
                onClick = {
                    if (mute) {
                        onUnmuteClick()
                    } else {
                        onMuteClick()
                    }
                },
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
}

@Composable
private fun PlayPauseIconButton(
    modifier: Modifier,
    playing: Boolean,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = {
            if (playing) {
                onPauseClick()
            } else {
                onPlayClick()
            }
        },
    ) {
        val icon = if (playing) {
            Icons.Default.Pause
        } else {
            Icons.Default.PlayArrow
        }
        Icon(
            painter = rememberVectorPainter(icon),
            contentDescription = if (playing) "pause" else "play",
            tint = Color.White,
        )
    }
}

@Composable
private fun PlayerProgress(
    modifier: Modifier,
    progress: Float,
    onProgressChange: (progress: Float) -> Unit,
) {
    var progressInChanging by remember {
        mutableFloatStateOf(progress)
    }
    var sliding by remember {
        mutableStateOf(false)
    }
    val displayProgress = if (sliding) {
        progressInChanging
    } else {
        progress
    }
    Slider(
        modifier = modifier,
        value = displayProgress,
        onValueChange = {
            progressInChanging = it
            sliding = true
        },
        onValueChangeFinished = {
            onProgressChange(progressInChanging)
            sliding = false
        },
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            activeTickColor = Color.White,
        )
    )
}
