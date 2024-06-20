package com.zhangke.fread.commonbiz.shared.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.video.ExoPlayerManager
import com.zhangke.framework.composable.video.LocalExoPlayerManager
import com.zhangke.fread.status.ui.video.full.FullScreenVideoPlayer

class FullVideoScreen(private val uri: Uri) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val playerManager = remember {
            ExoPlayerManager()
        }
        CompositionLocalProvider(
            LocalExoPlayerManager provides playerManager
        ) {
            FullScreenVideoPlayer(
                uri = uri,
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
