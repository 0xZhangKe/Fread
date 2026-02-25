package com.zhangke.fread.commonbiz.shared.screen

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.status.ui.video.full.FullScreenVideoPlayer
import kotlinx.serialization.Serializable

@Serializable
data class FullVideoScreenNavKey(val uri: String): NavKey

@Composable
fun FullVideoScreen(uri: String) {
    val backStack = LocalNavBackStack.currentOrThrow
    FullScreenVideoPlayer(
        uri = uri.toPlatformUri(),
        onBackClick = backStack::removeLastOrNull,
    )
}
