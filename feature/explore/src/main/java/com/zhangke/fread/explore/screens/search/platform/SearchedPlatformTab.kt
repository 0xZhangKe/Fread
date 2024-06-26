package com.zhangke.fread.explore.screens.search.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.explore.R
import com.zhangke.fread.status.model.IdentityRole

class SearchedPlatformTab(private val role: IdentityRole, private val query: String) : BasePagerTab() {
    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(R.string.explorer_search_tab_title_server),
        )

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val viewModel = screen.getViewModel<SearchPlatformViewModel>()
    }
}
