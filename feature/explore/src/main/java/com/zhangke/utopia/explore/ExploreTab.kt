package com.zhangke.utopia.explore

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.utopia.explore.pages.home.ExploreHomePage

class ExploreTab(private val tabIndex: UShort) : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Default.Explore)
            return remember {
                TabOptions(
                    index = tabIndex, title = "Explore", icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        ExploreHomePage()
    }
}