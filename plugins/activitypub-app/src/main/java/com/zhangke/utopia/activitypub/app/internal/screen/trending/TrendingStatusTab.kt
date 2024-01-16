package com.zhangke.utopia.activitypub.app.internal.screen.trending

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode

class TrendingStatusTab(private val baseUrl: FormalBaseUrl) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(id = R.string.activity_pub_content_tab_trending)
        )

    @Composable
    override fun Screen.TabContent() {
        val viewModel = getViewModel<TrendingStatusViewModel>().getSubViewModel(baseUrl)
        val statusFlow by viewModel.statusFlow.collectAsState()
        val statusList = statusFlow.collectAsLazyPagingItems()
        InlineVideoLazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(statusList) { index, status ->
                if (status != null) {
                    FeedsStatusNode(
                        modifier = Modifier.fillMaxWidth(),
                        status = status.status,
                        bottomPanelInteractions = status.bottomInteractions,
                        moreInteractions = status.moreInteractions,
                        onInteractive = viewModel::onInteractive,
                        indexInList = index,
                    )
                }
            }
        }
    }
}
