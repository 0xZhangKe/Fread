package com.zhangke.fread.explore.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.ic_explorer
import org.jetbrains.compose.resources.painterResource

class ExploreTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_explorer)
            return remember {
                TabOptions(
                    title = "Explore",
                    icon = icon,
                    index = tabIndex
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(ExplorerScreen())
    }
}
