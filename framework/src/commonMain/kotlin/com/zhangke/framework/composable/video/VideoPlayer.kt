package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.utils.PlatformUri

@Composable
expect fun VideoPlayer(
    uri: PlatformUri,
    playWhenReady: Boolean,
    modifier: Modifier = Modifier,
    state: VideoState = rememberVideoPlayerState(),
    useController: Boolean = false,
)