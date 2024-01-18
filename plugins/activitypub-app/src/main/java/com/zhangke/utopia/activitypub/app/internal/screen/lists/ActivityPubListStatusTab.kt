package com.zhangke.utopia.activitypub.app.internal.screen.lists

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.screen.content.ActivityPubListStatusContent

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
}
