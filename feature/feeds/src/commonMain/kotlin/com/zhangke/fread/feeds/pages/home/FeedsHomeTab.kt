package com.zhangke.fread.feeds.pages.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.ic_home
import org.jetbrains.compose.resources.painterResource

class FeedsHomeTab() : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_home)
            return remember {
                TabOptions(
                    title = "Home",
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        FeedsContentHomeScreen()
    }
}
