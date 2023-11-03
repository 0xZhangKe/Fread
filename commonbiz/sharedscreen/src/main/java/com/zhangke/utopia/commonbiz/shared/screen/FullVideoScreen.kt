package com.zhangke.utopia.commonbiz.shared.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.utopia.status.ui.video.full.FullScreenVideoPlayer

class FullVideoScreen(private val uri: Uri) : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        FullScreenVideoPlayer(
            uri = uri,
            onBackClick = navigator::pop,
        )
    }
}
