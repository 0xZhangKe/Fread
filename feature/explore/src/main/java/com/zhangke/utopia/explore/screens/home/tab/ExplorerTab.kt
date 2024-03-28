package com.zhangke.utopia.explore.screens.home.tab

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions

class ExplorerTab : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = "Explorer"
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        TODO("Not yet implemented")
    }
}