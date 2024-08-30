package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.utils.PlatformUri

@Composable
actual fun VideoPlayer(
    uri: PlatformUri,
    playWhenReady: Boolean,
    modifier: Modifier,
    state: VideoState,
    useController: Boolean,
) {
    // TODO: ios video player
}