package com.zhangke.utopia.feeds.pages.home.feeds

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.text.style.TextAlign
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
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.composable.textString
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.voyager.tryPush
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.StatusListPlaceholder
import com.zhangke.utopia.status.ui.common.NewStatusNotifyBar
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MixedContentScreen(private val configId: Long) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val snackbarHostState = LocalSnackbarHostState.current
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<MixedContentViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        MixedContentUi(
            uiState = uiState,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            onInteractive = viewModel::onInteractive,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onUserInfoClick = viewModel::onUserInfoClick,
            onVoted = viewModel::onVoted,
            nestedScrollConnection = nestedScrollConnection,
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.tryPush(it)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun MixedContentUi(
        uiState: MixedContentUiState,
        newStatusNotifyFlow: SharedFlow<Unit>,
        onUserInfoClick: (BlogAuthor) -> Unit,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onVoted: (Status, List<BlogPoll.Option>) -> Unit,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        if (uiState.feeds.isEmpty()) {
            if (uiState.showPagingLoadingPlaceholder) {
                StatusListPlaceholder()
            } else if (uiState.pageErrorContent != null) {
                InitErrorContent(uiState.pageErrorContent)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                val state = rememberLoadableInlineVideoLazyColumnState(
                    refreshing = uiState.refreshing,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                )
                LoadableInlineVideoLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .applyNestedScrollConnection(nestedScrollConnection),
                    state = state,
                    refreshing = uiState.refreshing,
                    loadState = uiState.loadMoreState,
                    contentPadding = PaddingValues(
                        bottom = 20.dp,
                    )
                ) {
                    itemsIndexed(
                        items = uiState.feeds,
                        key = { _, item ->
                            item.statusUiState.status.id
                        },
                    ) { index, item ->
                        FeedsStatusNode(
                            modifier = Modifier.fillMaxWidth(),
                            status = item.statusUiState,
                            role = item.role,
                            onUserInfoClick = onUserInfoClick,
                            onInteractive = onInteractive,
                            indexInList = index,
                            onVoted = onVoted,
                        )
                    }
                }
                var showNewStatusNotifyBar by remember {
                    mutableStateOf(false)
                }
                ConsumeFlow(newStatusNotifyFlow) {
                    if (state.lazyListState.firstVisibleItemIndex > 0) {
                        showNewStatusNotifyBar = true
                    }
                }
                val coroutineScope = rememberCoroutineScope()
                AnimatedVisibility(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .align(Alignment.TopCenter),
                    visible = showNewStatusNotifyBar,
                ) {
                    NewStatusNotifyBar(
                        modifier = Modifier,
                        onClick = {
                            showNewStatusNotifyBar = false
                            coroutineScope.launch {
                                if (uiState.feeds.isNotEmpty()) {
                                    state.lazyListState.animateScrollToItem(0)
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun InitErrorContent(errorMessage: TextString) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 56.dp, end = 16.dp),
                text = textString(text = errorMessage),
                textAlign = TextAlign.Center,
            )
        }
    }
}
