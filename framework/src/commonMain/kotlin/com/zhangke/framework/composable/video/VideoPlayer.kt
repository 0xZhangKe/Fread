package com.zhangke.framework.composable.video

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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.Log
import com.zhangke.framework.utils.PlatformUri
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun VideoPlayer(
    uri: PlatformUri,
    playWhenReady: Boolean,
    modifier: Modifier = Modifier,
    state: VideoState = rememberVideoPlayerState(),
    videoPlayerManager: VideoPlayerManager = LocalVideoPlayerManager.current,
) {
    var playErrorInfo: String? by remember {
        mutableStateOf(null)
    }
    var playAspectRatio by remember {
        mutableFloatStateOf(1.778F)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val videoPlayer = remember(uri, videoPlayerManager) {
        videoPlayerManager.obtainPlayer(uri, lifecycle).apply {
            prepare(state, playWhenReady)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        videoPlayer.Content(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(playAspectRatio),
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

    LaunchedEffect(state.targetSeekTo) {
        videoPlayer.seekTo(state.targetSeekTo)
    }
    LaunchedEffect(uri) {
        while (true) {
            delay(240)
            state.updatePosition(videoPlayer.currentPosition)
            state.updateDuration(videoPlayer.duration)
        }
    }
    LaunchedEffect(state.playerVolume) {
        if (videoPlayer.volume == state.playerVolume) return@LaunchedEffect
        videoPlayer.volume = state.playerVolume
    }

    LaunchedEffect(state.playing) {
        if (videoPlayer.isPlaying == state.playing) return@LaunchedEffect
        if (state.playing) {
            val diff = abs(videoPlayer.duration - videoPlayer.currentPosition)
            if (diff < 100L) {
                videoPlayer.seekTo(0L)
            }
            videoPlayer.play()
        } else {
            videoPlayer.pause()
        }
    }

    val listener = remember(uri) {
        object : PlatformVideoPlayerListener() {
            override fun onVideoSizeChanged(aspectRatio: Float) {
                playAspectRatio = aspectRatio
            }

            override fun onPlayerError(error: String?) {
                playErrorInfo = error
            }

            override fun onPlaybackEndChanged(ended: Boolean) {
                state.onPlaybackEndChanged(ended)
            }

            override fun onVolumeChanged(volume: Float) {
                state.onVolumeChanged(volume)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                state.onIsPlayingChanged(isPlaying)
            }
        }
    }

    DisposableEffect(uri) {
        videoPlayer.addListener(listener)
        onDispose {
            Log.d("PlayerManager") { "onDispose" }
            state.updatePosition(videoPlayer.currentPosition)
            videoPlayer.removeListener(listener)
            videoPlayerManager.recyclePlayer(uri)
        }
    }
}