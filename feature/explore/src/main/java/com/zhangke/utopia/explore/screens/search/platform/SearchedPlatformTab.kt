package com.zhangke.utopia.explore.screens.search.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.utopia.explore.R

class SearchedPlatformTab(private val query: String): PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(R.string.explorer_search_tab_title_server),
        )

    @Composable
    override fun Screen.TabContent() {
        TODO("Not yet implemented")
    }
}