package com.zhangke.framework.composable.video

import android.net.Uri
import android.util.Log
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.zhangke.framework.utils.toMediaSource
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    uri: Uri,
    playWhenReady: Boolean,
    state: VideoState = rememberVideoPlayerState(),
) {
    val context = LocalContext.current
    val playerListener = remember(uri) {
        object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.d("PlayerManager", "onPlaybackStateChanged:$playbackState")
                state.onPlaybackStateChanged(playbackState)
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
    val playerManager = LocalInlineExoPlayerManager.current
    val exoPlayer = remember(uri) {
//        playerManager.obtainPlayer(context, uri, lifecycle).apply {
        ExoPlayer.Builder(context).build().apply {

            addListener(playerListener)
            setMediaSource(uri.toMediaSource())
            prepare()
            seekTo(state.playerPosition)
            volume = state.playerVolume
            this.playWhenReady = false
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            repeatMode = Player.REPEAT_MODE_OFF




//            addListener(playerListener)
//            this.playWhenReady = true
//            volume = state.playerVolume
//            repeatMode = Player.REPEAT_MODE_OFF
////            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
//            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
//            setMediaSource(uri.toMediaSource())
//            prepare()
//            seekTo(state.playerPosition)
        }
    }
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
        update = {
//            exoPlayer.setVideoSurfaceView(it)
        },
    )
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
