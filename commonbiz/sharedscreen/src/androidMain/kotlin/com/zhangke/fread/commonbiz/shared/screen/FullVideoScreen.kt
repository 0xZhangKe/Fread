package com.zhangke.fread.commonbiz.shared.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.video.ExoPlayerManager
import com.zhangke.framework.composable.video.LocalExoPlayerManager
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.status.ui.video.full.FullScreenVideoPlayer
import kotlinx.serialization.Serializable

@Serializable
data class FullVideoScreenNavKey(val uri: String): NavKey

@Composable
fun FullVideoScreen(uri: String) {
    val backStack = LocalNavBackStack.currentOrThrow
    val playerManager = remember {
        ExoPlayerManager()
    }
    CompositionLocalProvider(
        LocalExoPlayerManager provides playerManager
    ) {
        FullScreenVideoPlayer(
            uri = uri.toPlatformUri(),
            onBackClick = backStack::removeLastOrNull,
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            playerManager.recycler()
        }
    }
}
