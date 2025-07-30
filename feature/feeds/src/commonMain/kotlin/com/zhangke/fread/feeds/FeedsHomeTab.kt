package com.zhangke.fread.feeds

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.framework.voyager.CurrentAnimatedScreen
import com.zhangke.fread.feeds.pages.home.ContentHomeScreen
import org.jetbrains.compose.resources.painterResource

class FeedsHomeTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_home)
            return remember {
                TabOptions(
                    index = tabIndex, title = "Home", icon = icon
                )
            }
        }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun Content() {
        SharedTransitionLayout {
            Navigator(
                screen = ContentHomeScreen(),
            ) {
                CurrentAnimatedScreen(it)
            }
        }
    }
}
