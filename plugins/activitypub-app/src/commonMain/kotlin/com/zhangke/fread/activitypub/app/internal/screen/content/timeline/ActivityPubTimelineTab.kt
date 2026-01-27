package com.zhangke.fread.activitypub.app.internal.screen.content.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.activitypub.app.internal.composable.ActivityPubTabNames
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.fread.commonbiz.shared.composable.InitErrorContent
import com.zhangke.fread.commonbiz.shared.composable.ObserveForFeedsConnection
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusListPlaceholder
import com.zhangke.fread.status.ui.common.ObserveScrollStopedPosition
import org.koin.compose.viewmodel.koinViewModel

internal class ActivityPubTimelineTab(
    private val locator: PlatformLocator,
    private val type: ActivityPubStatusSourceType,
    private val listId: String? = null,
    private val listTitle: String? = null,
) : BaseTab() {

    override val options: TabOptions
        @Composable get() = TabOptions(
            title = when (type) {
                ActivityPubStatusSourceType.TIMELINE_HOME -> ActivityPubTabNames.homeTimeline
                ActivityPubStatusSourceType.TIMELINE_LOCAL -> ActivityPubTabNames.localTimeline
                ActivityPubStatusSourceType.TIMELINE_PUBLIC -> ActivityPubTabNames.publicTimeline
                ActivityPubStatusSourceType.LIST -> listTitle!!
            }
        )

    @Composable
    override fun Content() {
        super.Content()
        val viewModel = koinViewModel<ActivityPubTimelineContainerViewModel>()
            .getSubViewModel(locator, type, listId)
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = LocalSnackbarHostState.current
        ActivityPubTimelineContent(
            uiState = uiState,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onJumpedToStatus = viewModel::onJumpedToStatus,
            onLoadPrevious = viewModel::onLoadPreviousPage,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onReadPositionIndexChanged = viewModel::updateMaxReadStatus,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
    }

    @Composable
    private fun ActivityPubTimelineContent(
        uiState: ActivityPubTimelineUiState,
        composedStatusInteraction: ComposedStatusInteraction,
        onJumpedToStatus: () -> Unit,
        onLoadPrevious: () -> Unit,
        onLoadMore: () -> Unit,
        onRefresh: () -> Unit,
        onReadPositionIndexChanged: (ActivityPubTimelineItem) -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.items.isEmpty()) {
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
                        initialFirstVisibleItemIndex = uiState.initialShowIndex,
                    )
                    val lazyListState = state.lazyListState
                    ObserveScrollStopedPosition(lazyListState) {
                        uiState.items.getOrNull(it)
                            ?.let { item ->
                                onReadPositionIndexChanged(item)
                            }
                    }
                    ObserveForFeedsConnection(
                        listState = lazyListState,
                        onRefresh = onRefresh,
                    )
                    LaunchedEffect(uiState.jumpToStatusId) {
                        if (!uiState.jumpToStatusId.isNullOrEmpty()) {
                            val jumpToIndex =
                                uiState.items.getIndexByIdOrNull(uiState.jumpToStatusId)
                            if (jumpToIndex >= 0 && jumpToIndex < uiState.items.size) {
                                onJumpedToStatus()
                                state.lazyListState.animateScrollToItem(jumpToIndex)
                            }
                        }
                    }
                    LoadableInlineVideoLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        onLoadPrevious = {
                            onLoadPrevious()
                        },
                        refreshing = uiState.refreshing,
                        loadState = uiState.loadMoreState,
                    ) {
                        itemsIndexed(
                            items = uiState.items,
                            key = { _, item ->
                                (item as ActivityPubTimelineItem.StatusItem).status.status.id
                            },
                        ) { index, item ->
                            FeedsStatusNode(
                                modifier = Modifier.fillMaxWidth(),
                                status = (item as ActivityPubTimelineItem.StatusItem).status,
                                composedStatusInteraction = composedStatusInteraction,
                                indexInList = index,
                            )
                        }
                    }
                }
            }
        }
    }
}
