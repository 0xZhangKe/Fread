package com.zhangke.utopia.feeds

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.utopia.feeds.pages.home.ContentHomeScreen

object FeedsHomeTab : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember {
                TabOptions(
                    index = 0u, title = "Home", icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
//        Navigator(FeedsHomeScreen())
        Navigator(ContentHomeScreen())
    }
}
