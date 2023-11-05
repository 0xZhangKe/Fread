package com.zhangke.utopia.pages

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.androidx.AndroidScreen
import com.zhangke.utopia.pages.main.MainPage

class UtopiaScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        MainPage()
//        val navigator = LocalNavigator.currentOrThrow
//        LaunchedEffect(Unit) {
//            navigator.push(InlineVideoPlayerScreen())
//            navigator.push(FullVideoScreen(
//                "https://video.twimg.com/ext_tw_video/1712110948700352512/pu/vid/avc1/720x1280/i43wruptl2R9KHAZ.mp4?tag=12".toUri()
//            ))
//        }
    }
}
