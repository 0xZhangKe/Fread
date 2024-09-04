package com.zhangke.fread.explore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.painterResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.ic_logo_small
import com.zhangke.fread.explore.screens.home.ExplorerHomeScreen

class ExploreTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_logo_small)
            return remember {
                TabOptions(
                    index = tabIndex, title = "Explore", icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(ExplorerHomeScreen())
    }
}
