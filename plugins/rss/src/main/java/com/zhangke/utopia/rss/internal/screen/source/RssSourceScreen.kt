package com.zhangke.utopia.rss.internal.screen.source

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel

class RssSourceScreen(

) : Screen {

    @Composable
    override fun Content() {
        val viewModel = getViewModel<RssSourceViewModel>()
    }
}
