package com.zhangke.utopia.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.debug.screens.poll.BlogPollTestScreen
import com.zhangke.utopia.debug.screens.video.InlineVideoPlayerScreen
import com.zhangke.utopia.pages.main.MainPage

class UtopiaScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        MainPage()
//        val navigator = LocalNavigator.currentOrThrow
//        LaunchedEffect(Unit) {
////            navigator.push(InlineVideoPlayerScreen())
//            navigator.push(FullVideoScreen(
//                "https://video.twimg.com/ext_tw_video/1712110948700352512/pu/vid/avc1/720x1280/i43wruptl2R9KHAZ.mp4?tag=12".toUri()
//            ))
//        }
    }
}
