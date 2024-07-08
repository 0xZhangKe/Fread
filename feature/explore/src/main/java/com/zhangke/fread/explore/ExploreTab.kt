package com.zhangke.fread.explore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.fread.explore.screens.home.ExplorerHomeScreen

class ExploreTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = painterResource(id = R.drawable.explorer_tab)
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
