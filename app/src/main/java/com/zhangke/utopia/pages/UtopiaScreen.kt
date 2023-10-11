package com.zhangke.utopia.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.utopia.debug.screens.poll.BlogPollTestScreen
import com.zhangke.utopia.debug.screens.video.InlineVideoPlayerScreen

class UtopiaScreen : AndroidScreen() {

    @Composable
    override fun Content() {
//        MainPage()
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(Unit) {
            navigator.push(InlineVideoPlayerScreen())
        }
    }
}
