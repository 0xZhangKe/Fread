package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eygraber.uri.Uri

@Composable
actual fun VideoPlayer(
    uri: Uri,
    playWhenReady: Boolean,
    modifier: Modifier,
    state: VideoState,
    useController: Boolean,
) {
    // TODO: ios video player
}