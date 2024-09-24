package com.zhangke.fread.feeds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.fread.feeds.pages.home.ContentHomeScreen

class FeedsHomeTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(id = R.drawable.ic_home)
            return remember {
                TabOptions(
                    index = tabIndex, title = "Home", icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(ContentHomeScreen())
    }
}
