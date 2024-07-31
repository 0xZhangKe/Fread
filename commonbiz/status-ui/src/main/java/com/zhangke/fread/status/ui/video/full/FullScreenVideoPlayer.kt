package com.zhangke.fread.status.ui.video.full

import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.video.VideoPlayer
import com.zhangke.framework.composable.video.rememberVideoPlayerState
import com.zhangke.fread.status.ui.video.VideoDurationFormatter

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun FullScreenVideoPlayer(
    modifier: Modifier = Modifier,
    uri: Uri,
    onBackClick: () -> Unit,
) {
    var panelVisible by remember {
        mutableStateOf(true)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .noRippleClick {
                panelVisible = !panelVisible
            }
            .background(color = Color.Black)
    ) {
        val videoState = rememberVideoPlayerState()
        VideoPlayer(
            modifier = Modifier,
            uri = uri,
            playWhenReady = true,
            state = videoState,
        )
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = panelVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                FullScreenVideoPlayerPanel(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(),
                    playing = videoState.playing,
                    mute = videoState.playerVolume <= 0F,
                    onPlayClick = {
                        videoState.play()
                    },
                    onPauseClick = {
                        videoState.pause()
                    },
                    playerPosition = videoState.playerPosition,
                    duration = videoState.duration,
                    onPositionChangeRequest = {
                        videoState.seekTo(it)
                    },
                    onMuteClick = {
                        videoState.mute()
                    },
                    onUnmuteClick = {
                        videoState.unmute()
                    },
                )
                FullScreenPlayerToolBar(
                    modifier = Modifier.align(Alignment.TopStart),
                    onBackClick = onBackClick,
                )
            }
        }
    }
}

@Composable
private fun FullScreenPlayerToolBar(
    modifier: Modifier,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = ToolbarTokens.TopAppBarHorizontalPadding)
            .height(ToolbarTokens.ContainerHeight)
    ) {
        Toolbar.BackButton(
            onBackClick = onBackClick,
            tint = Color.White,
        )
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
            .padding(bottom = 16.dp),
    ) {
        PlayerProgress(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
            val icon = if (mute) {
                Icons.AutoMirrored.Filled.VolumeOff
            } else {
                Icons.AutoMirrored.Filled.VolumeUp
            }
            SimpleIconButton(
                onClick = {
                    if (mute) {
                        onUnmuteClick()
                    } else {
                        onMuteClick()
                    }
                },
                tint = Color.White,
                imageVector = icon,
                contentDescription = if (mute) "unmute" else "mute",
            )
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
        if (progress.isNaN()) {
            0F
        } else {
            progress
        }
    }
    Slider(
        modifier = modifier,
        value = displayProgress.coerceAtLeast(0F).coerceAtMost(1F),
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
