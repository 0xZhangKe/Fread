package com.zhangke.utopia.activitypub.app.internal.screen.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.status.status.model.Status

class ActivityPubListStatusTab(
    private val baseUrl: FormalBaseUrl,
    private val listId: String,
    private val listTitle: String,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = listTitle
        )

    @Composable
    override fun Screen.TabContent() {
        val viewModel =
            getViewModel<ActivityPubListStatusViewModel>().getSubViewModel(baseUrl, listId)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubListStatusContent(
            uiState = uiState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onInteractive = viewModel::onInteractive,
        )
        ConsumeFlow(viewModel.snackMessage) {
            // TODO handle this message in UI
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ActivityPubListStatusContent(
        uiState: ActivityPubListStatusUiState,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
    ) {
        val state = rememberLoadableInlineVideoLazyColumnState(
            refreshing = uiState.refreshing,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
        )
        Box(modifier = Modifier.fillMaxSize()) {
            LoadableInlineVideoLazyColumn(
                state = state,
                modifier = Modifier.fillMaxSize(),
                refreshing = uiState.refreshing,
                loading = uiState.loadMoreState == LoadState.Loading,
                contentPadding = PaddingValues(
                    bottom = 20.dp,
                )
            ) {
                itemsIndexed(
                    items = uiState.status,
                    key = { _, item ->
                        item.status.id
                    },
                ) { index, status ->
                    FeedsStatusNode(
                        modifier = Modifier.fillMaxWidth(),
                        status = status.status,
                        bottomPanelInteractions = status.bottomInteractions,
                        moreInteractions = status.moreInteractions,
                        onInteractive = onInteractive,
                        indexInList = index,
                    )
                }
            }
        }
    }
}
