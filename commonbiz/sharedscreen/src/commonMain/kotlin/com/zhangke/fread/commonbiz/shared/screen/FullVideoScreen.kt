package com.zhangke.fread.commonbiz.shared.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.video.LocalVideoPlayerManager
import com.zhangke.framework.composable.video.rememberVideoPlayerManager
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.ui.video.full.FullScreenVideoPlayer

class FullVideoScreen(private val uri: String) : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val playerManager = rememberVideoPlayerManager()
        CompositionLocalProvider(
            LocalVideoPlayerManager provides playerManager
        ) {
            FullScreenVideoPlayer(
                uri = uri.toPlatformUri(),
                onBackClick = navigator::pop,
            )
        }
        DisposableEffect(Unit) {
            onDispose {
                playerManager.recycler()
            }
        }
    }
}