package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eygraber.uri.Uri

@Composable
expect fun VideoPlayer(
    uri: Uri,
    playWhenReady: Boolean,
    modifier: Modifier = Modifier,
    state: VideoState = rememberVideoPlayerState(),
    useController: Boolean = false,
)