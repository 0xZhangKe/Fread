package com.zhangke.fread.bluesky.internal.screen.feeds.list

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole

class BskyFeedsExplorerPage(private val role: IdentityRole) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<BskyFeedsExplorerViewModel, BskyFeedsExplorerViewModel.Factory>{
            it.create(role)
        }

    }

    @Composable
    private fun BskyFeedsExplorerContent(
        uiState: BskyFeedsExplorerUiState,
    ) {

    }
}
