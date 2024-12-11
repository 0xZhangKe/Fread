package com.zhangke.fread.bluesky.internal.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.common.page.BasePagerTab

class BlueskyHomeTab(
    private val configId: Long,
    private val isLatestContent: Boolean,
) : BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?
    ) {
        super.TabContent(screen, nestedScrollConnection)
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            screen.getViewModel<BlueskyHomeContainerViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        BlueskyHomeContent(
            uiState = uiState,
        )
    }

    @Composable
    private fun BlueskyHomeContent(
        uiState: BlueskyHomeUiState,

        ) {

    }
}
