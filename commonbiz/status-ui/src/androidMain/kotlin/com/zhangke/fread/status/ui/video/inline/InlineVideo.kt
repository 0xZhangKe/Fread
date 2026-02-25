package com.zhangke.fread.status.ui.video.inline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.inline.LocalPlayableIndexRecorder
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.video.VideoPlayer
import com.zhangke.framework.composable.video.rememberVideoPlayerController
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.config.LocalFreadConfigManager

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun InlineVideo(
    aspectRatio: Float?,
    coverImage: String?,
    indexInList: Int,
    style: InlineVideoPlayerStyle = InlineVideoPlayerDefault.defaultStyle,
    uri: PlatformUri,
    onClick: () -> Unit,
) {
    val playableIndexRecorder = LocalPlayableIndexRecorder.current
    playableIndexRecorder?.recordePlayableIndex(indexInList)
    val playWhenReady =
        playableIndexRecorder != null && playableIndexRecorder.currentActiveIndex == indexInList
    InlineVideoShell(
        aspectRatio = aspectRatio ?: style.defaultMediaAspect,
        style = style,
    ) {
        val freadConfigManager = LocalFreadConfigManager.current
        InlineVideoPlayer(
            uri = uri,
            coverImage = coverImage,
            autoPlay = freadConfigManager.autoPlayInlineVideo,
            playWhenReady = playWhenReady,
            onClick = onClick,
            onPlayManually = { playableIndexRecorder?.changeActiveIndex(indexInList) },
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
    uri: PlatformUri,
    coverImage: String?,
    autoPlay: Boolean,
    playWhenReady: Boolean,
    onClick: () -> Unit,
    onPlayManually: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .noRippleClick(onClick = onClick)
    ) {
        if (autoPlay && playWhenReady) {
            val videoController = rememberVideoPlayerController(
                mediaUrl = uri.toString(),
                initialContentScale = ContentScale.Fit,
                autoPlay = true,
                initialMuted = true,
                isLooping = false,
            )
            VideoPlayer(
                modifier = Modifier.fillMaxSize().noRippleClick(onClick = onClick),
                controller = videoController,
            )
            InlineVideoControlPanel(
                playEnded = videoController.hasPlaybackEnded,
                mute = videoController.isMuted,
                onPlayClick = {
                    onPlayManually()
                    videoController.play()
                },
                onMuteClick = { mute ->
                    if (mute) {
                        videoController.mute()
                    } else {
                        videoController.unmute()
                    }
                },
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AutoSizeImage(
                    url = coverImage.orEmpty(),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
                PlayVideoIconButton(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                    onClick = {
                        if (autoPlay) {
                            onPlayManually()
                        } else {
                            onClick()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun InlineVideoControlPanel(
    playEnded: Boolean,
    mute: Boolean,
    onPlayClick: () -> Unit,
    onMuteClick: (mute: Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (playEnded) {
            PlayVideoIconButton(
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.Center),
                onClick = onPlayClick,
            )
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

@Composable
internal fun PlayVideoIconButton(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    SimpleIconButton(
        modifier = modifier,
        onClick = onClick,
        imageVector = Icons.Default.PlayCircleOutline,
        tint = Color.White,
        contentDescription = "Play",
    )
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
