package com.zhangke.fread.status.ui.video.full

import android.net.Uri
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.ui.utils.toMediaSource
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

        var playWhenReady by remember {
            mutableStateOf(true)
        }

        val exoPlayer = remember(uri) {
            ExoPlayer.Builder(context)
                .build()
                .apply {
                    setMediaSource(uri.toMediaSource())
                    prepare()
                    seekTo(playerPosition)
                    this.playWhenReady = true
                    volume = 1F
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                    repeatMode = Player.REPEAT_MODE_ALL
                }
        }
        LaunchedEffect(playWhenReady) {
            exoPlayer.playWhenReady = playWhenReady
        }
        LaunchedEffect(playWhenReady) {
            exoPlayer.playWhenReady = playWhenReady
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
                SurfaceView(it).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                    exoPlayer.setVideoSurfaceView(this)
                }
            },
        )
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = panelVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                FullScreenVideoPlayerPanel(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    playWhenReady = playWhenReady,
                    onPlayClick = {
                        playWhenReady = true
                    },
                    onPauseClick = {
                        playWhenReady = false
                    },
                    playerPosition = playerPosition,
                    duration = exoPlayer.duration,
                    onPositionChangeRequest = {
                        exoPlayer.seekTo(it)
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
    playWhenReady: Boolean,
    playerPosition: Long,
    duration: Long,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPositionChangeRequest: (position: Long) -> Unit,
) {
    val progress = playerPosition / duration.toFloat()
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0x05000000), Color(0xF9000000))))
            .padding(bottom = 16.dp)
            .height(40.dp),
    ) {
        val (controlBtnRef, progressRef, timeRef) = createRefs()
        PlayPauseIconButton(
            modifier = Modifier.constrainAs(controlBtnRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, 8.dp)
                width = Dimension.wrapContent
            },
            playWhenReady = playWhenReady,
            onPauseClick = onPauseClick,
            onPlayClick = onPlayClick,
        )
        PlayerProgress(
            modifier = Modifier
                .constrainAs(progressRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(timeRef.top)
                    start.linkTo(controlBtnRef.end)
                    end.linkTo(parent.end, 4.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
            progress = progress,
            onProgressChange = { progress ->
                onPositionChangeRequest((duration * progress).toLong())
            },
        )
        Text(
            modifier = Modifier.constrainAs(timeRef) {
                top.linkTo(progressRef.bottom)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end, 12.dp)
            },
            fontSize = 12.sp,
            text = VideoDurationFormatter.formatVideoProgressDesc(playerPosition, duration),
            color = Color.White,
        )
    }
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
