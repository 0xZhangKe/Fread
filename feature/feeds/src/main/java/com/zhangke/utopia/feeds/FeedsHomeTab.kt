package com.zhangke.utopia.feeds

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.core.net.toUri
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.krouter.KRouter
import com.zhangke.utopia.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.utopia.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.utopia.feeds.pages.home.FeedsHomeScreenContent
import com.zhangke.utopia.feeds.pages.home.FeedsHomeViewModel
import com.zhangke.utopia.feeds.pages.manager.add.AddFeedsManagerScreen
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent

object FeedsHomeTab : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember {
                TabOptions(
                    index = 0u, title = "Home", icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: FeedsHomeViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        FeedsHomeScreenContent(
            uiState = uiState,
            onTabSelected = viewModel::onPageChanged,
            onLoadMore = viewModel::onLoadMore,
            onRefresh = viewModel::onRefresh,
            onAddFeedsClick = {
                navigator.push(AddFeedsManagerScreen())
            },
            onServerItemClick = { server ->
                val screen =
                    KRouter.route<AndroidScreen>(server.uri.toString())!!
                navigator.push(screen)
            },
        )
    }
}
