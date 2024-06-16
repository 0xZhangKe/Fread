package com.zhangke.fread.commonbiz.shared.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.fread.status.ui.video.full.FullScreenVideoPlayer

class FullVideoScreen(private val uri: Uri) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        FullScreenVideoPlayer(
            uri = uri,
            onBackClick = navigator::pop,
        )
    }
}
