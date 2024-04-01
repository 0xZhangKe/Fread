package com.zhangke.utopia.explore.screens.home.tab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.explore.R
import com.zhangke.utopia.explore.model.ExplorerItem
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.RecommendAuthorUi
import com.zhangke.utopia.status.ui.hashtag.HashtagUi
import com.zhangke.utopia.status.uri.FormalUri

class ExplorerFeedsTab(
    private val type: ExplorerFeedsTabType,
    private val accountUri: FormalUri,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = when (type) {
                ExplorerFeedsTabType.STATUS -> stringResource(R.string.explorer_tab_status_title)
                ExplorerFeedsTabType.USERS -> stringResource(R.string.explorer_tab_users_title)
                ExplorerFeedsTabType.HASHTAG -> stringResource(R.string.explorer_tab_hashtag_title)
            }
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            getViewModel<ExplorerFeedsContainerViewModel>().getSubViewModel(type, accountUri)
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.push(it)
        }
        ExplorerFeedsTabContent(
            uiState = uiState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onInteractive = viewModel::onInteractive,
            onUserInfoClick = viewModel::onUserInfoClick,
            onVoted = viewModel::onVoted,
            onHashtagClick = viewModel::onHashtagClick,
            onFollowClick = viewModel::onFollowClick,
            onUnfollowClick = viewModel::onUnfollowClick,
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ExplorerFeedsTabContent(
        uiState: CommonLoadableUiState<ExplorerItem>,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onUserInfoClick: (BlogAuthor) -> Unit,
        onFollowClick: (BlogAuthor) -> Unit,
        onHashtagClick: (Hashtag) -> Unit,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
        onVoted: (Status, List<BlogPoll.Option>) -> Unit,
        onUnfollowClick: (BlogAuthor) -> Unit,
    ) {
        val state = rememberLoadableInlineVideoLazyColumnState(
            refreshing = uiState.refreshing,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
        )
        LoadableInlineVideoLazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = state,
            refreshing = uiState.refreshing,
            loadState = uiState.loadMoreState,
            contentPadding = PaddingValues(
                bottom = 20.dp,
            )
        ) {
            itemsIndexed(
                items = uiState.dataList,
                key = { _, item ->
                    item.id
                },
            ) { index, item ->
                ExplorerItemUi(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    onUserInfoClick = onUserInfoClick,
                    onInteractive = onInteractive,
                    indexInList = index,
                    onVoted = onVoted,
                    onFollowClick = onFollowClick,
                    onHashtagClick = onHashtagClick,
                    onUnfollowClick = onUnfollowClick,
                )
            }
        }
    }

    @Composable
    private fun ExplorerItemUi(
        modifier: Modifier,
        item: ExplorerItem,
        indexInList: Int,
        onUserInfoClick: (BlogAuthor) -> Unit,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
        onVoted: (Status, List<BlogPoll.Option>) -> Unit,
        onHashtagClick: (Hashtag) -> Unit,
        onFollowClick: (BlogAuthor) -> Unit,
        onUnfollowClick: (BlogAuthor) -> Unit,
    ) {
        when (item) {
            is ExplorerItem.ExplorerStatus -> {
                FeedsStatusNode(
                    modifier = modifier,
                    status = item.status,
                    indexInList = indexInList,
                    onInteractive = onInteractive,
                    onUserInfoClick = onUserInfoClick,
                    onVoted = onVoted,
                )
            }

            is ExplorerItem.ExplorerUser -> {
                RecommendAuthorUi(
                    modifier = modifier,
                    author = item.user,
                    following = item.following,
                    onInfoClick = { onUserInfoClick(item.user) },
                    onFollowClick = onFollowClick,
                    onUnfollowClick = onUnfollowClick,
                )
            }

            is ExplorerItem.ExplorerHashtag -> {
                HashtagUi(
                    modifier = modifier,
                    tag = item.hashtag,
                    onClick = onHashtagClick,
                )
            }
        }
    }
}
