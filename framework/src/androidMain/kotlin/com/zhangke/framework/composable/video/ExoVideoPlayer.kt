package com.zhangke.framework.composable.video

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toAndroidUri
import com.zhangke.framework.utils.toMediaSource

class ExoVideoPlayer(
    private var exoPlayer: ExoPlayer?,
) : PlatformVideoPlayer {

    @OptIn(UnstableApi::class)
    override fun prepare(
        state: VideoState,
        playWhenReady: Boolean,
    ) {
        exoPlayer?.let {
            it.volume = state.playerVolume
            it.playWhenReady = playWhenReady
            it.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            it.prepare()
            it.seekTo(state.targetSeekTo)
        }
    }

    override fun play() {
        exoPlayer?.play()
    }

    override fun pause() {
        exoPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    override fun stop() {
        exoPlayer?.stop()
    }

    override fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    override var volume: Float
        get() = exoPlayer?.volume ?: 0f
        set(value) {
            exoPlayer?.volume = value
        }

    override val duration: Long
        get() = exoPlayer?.duration ?: 0L

    override val currentPosition: Long
        get() = exoPlayer?.currentPosition ?: 0L

    override val isPlaying: Boolean
        get() = exoPlayer?.isPlaying ?: false

    @OptIn(UnstableApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        AndroidView(
            modifier = modifier,
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
                it.useController = false
            },
        )
    }

    override fun addListener(listener: PlatformVideoPlayerListener) {
        exoPlayer?.addListener(listener.exoPlayerListener)
    }

    override fun removeListener(listener: PlatformVideoPlayerListener) {
        exoPlayer?.removeListener(listener.exoPlayerListener)
    }

    class Factory(
        private val context: Context,
    ) : PlatformVideoPlayer.Factory {
        @UnstableApi
        override fun create(uri: PlatformUri): PlatformVideoPlayer {
            return ExoVideoPlayer(
                exoPlayer = ExoPlayer.Builder(context)
                    .build().apply {
                        setMediaSource(uri.toAndroidUri().toMediaSource())
                    },
            )
        }
    }
}

