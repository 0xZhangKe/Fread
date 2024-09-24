package com.zhangke.fread.explore.screens.search.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.explore.Res
import com.zhangke.fread.explore.explorer_search_tab_title_server
import com.zhangke.fread.status.model.IdentityRole
import org.jetbrains.compose.resources.stringResource

class SearchedPlatformTab(private val role: IdentityRole, private val query: String) :
    BasePagerTab() {
    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(Res.string.explorer_search_tab_title_server),
        )

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val viewModel = screen.getViewModel<SearchPlatformViewModel>()

        val snackbarHostState = LocalSnackbarHostState.current

        ConsumeSnackbarFlow(
            hostState = snackbarHostState,
            messageTextFlow = viewModel.snackMessageFlow,
        )
    }
}
