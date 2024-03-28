package com.zhangke.utopia.explore.screens.home.tab

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.utopia.explore.R

class ExplorerFeedsTab(
    private val type: ExplorerFeedsTabType,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = when(type){
                ExplorerFeedsTabType.STATUS-> stringResource(R.string.explorer_tab_status_title)
                ExplorerFeedsTabType.USERS-> stringResource(R.string.explorer_tab_users_title)
                ExplorerFeedsTabType.HASHTAG-> stringResource(R.string.explorer_tab_hashtag_title)
            }
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val viewModel = getViewModel<ExplorerFeedsContainerViewModel>().getSubViewModel(type)

    }
}
