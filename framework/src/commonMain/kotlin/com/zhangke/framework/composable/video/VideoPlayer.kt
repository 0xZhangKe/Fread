package com.zhangke.framework.composable.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.Log
import com.zhangke.framework.utils.PlatformUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs

@Composable
fun VideoPlayer(
    uri: PlatformUri,
    playWhenReady: Boolean,
    modifier: Modifier = Modifier,
    aspectRatio: VideoAspectRatio = VideoAspectRatio.ScaleToFit,
    state: VideoState = rememberVideoPlayerState(),
    videoPlayerManager: VideoPlayerManager = LocalVideoPlayerManager.current,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val controller = remember(uri, videoPlayerManager) {
        videoPlayerManager.obtainPlayer(uri, lifecycle).apply {
            prepare(state, playWhenReady)
        }
    }

    val playErrorInfo by controller.errorFlow.collectAsState(null)
    val isLoading by controller.isLoadingFlow.collectAsState(false)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        controller.Content(
            aspectRatio = aspectRatio,
            modifier = Modifier.fillMaxSize(),
        )
        if (playErrorInfo.isNullOrBlank().not()) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = playErrorInfo.orEmpty(),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
            )
        }
    }

    LaunchedEffect(controller) {
        while (isActive) {
            delay(250)
            state.updateDuration(controller.duration.inWholeMilliseconds)
            state.updatePosition(controller.currentPosition.inWholeMilliseconds)
        }
    }
    LaunchedEffect(controller) {
        snapshotFlow { state.targetSeekTo }.collect { position ->
            controller.seekTo(position)
        }
    }
    LaunchedEffect(controller) {
        snapshotFlow { state.playerVolume }.collect { volume ->
            controller.setVolume(volume)
        }
    }
    LaunchedEffect(controller) {
        snapshotFlow { state.playing }.collect { playing ->
            if (playing) {
                val diff = abs(controller.duration.inWholeMilliseconds - controller.currentPosition.inWholeMilliseconds)
                if (diff < 100L) {
                    controller.seekTo(0L)
                }
                controller.play()
            } else {
                controller.pause()
            }
        }
    }

    LaunchedEffect(controller) {
        controller.volumeFlow.collect { volume ->
            state.onVolumeChanged(volume)
        }
    }
    LaunchedEffect(controller) {
        controller.hasVideoEndedFlow.collect { ended ->
            state.onPlaybackEndChanged(ended)
        }
    }
    LaunchedEffect(controller) {
        controller.isPlayingFlow.collect { isPlaying ->
            state.onIsPlayingChanged(isPlaying)
        }
    }

    DisposableEffect(uri) {
        onDispose {
            Log.d("PlayerManager") { "onDispose" }
            videoPlayerManager.recyclePlayer(uri)
        }
    }
}