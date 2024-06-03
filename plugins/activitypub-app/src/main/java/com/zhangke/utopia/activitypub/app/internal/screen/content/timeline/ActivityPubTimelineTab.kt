package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.utopia.activitypub.app.internal.composable.ActivityPubTabNames
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.commonbiz.shared.composable.InitErrorContent
import com.zhangke.utopia.commonbiz.shared.composable.ObserveToImmersive
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import com.zhangke.utopia.status.ui.StatusListPlaceholder

class ActivityPubTimelineTab(
    private val role: IdentityRole,
    private val type: ActivityPubStatusSourceType,
    private val listId: String? = null,
    private val listTitle: String? = null,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = when (type) {
                ActivityPubStatusSourceType.TIMELINE_HOME -> ActivityPubTabNames.homeTimeline
                ActivityPubStatusSourceType.TIMELINE_LOCAL -> ActivityPubTabNames.localTimeline
                ActivityPubStatusSourceType.TIMELINE_PUBLIC -> ActivityPubTabNames.publicTimeline
                ActivityPubStatusSourceType.LIST -> listTitle!!
            }
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val viewModel = getViewModel<ActivityPubTimelineContainerViewModel>()
            .getSubViewModel(role, type, listId)
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = LocalSnackbarHostState.current
        ActivityPubTimelineContent(
            uiState = uiState,
            nestedScrollConnection = nestedScrollConnection,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onLoadPrevious = viewModel::onLoadPreviousPage,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ActivityPubTimelineContent(
        uiState: ActivityPubTimelineUiState,
        nestedScrollConnection: NestedScrollConnection?,
        composedStatusInteraction: ComposedStatusInteraction,
        onLoadPrevious: () -> Unit,
        onLoadMore: () -> Unit,
        onRefresh: () -> Unit,
    ) {
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
                )
                val lazyListState = state.lazyListState
                ObserveToImmersive(lazyListState)
                LoadableInlineVideoLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .applyNestedScrollConnection(nestedScrollConnection),
                    state = state,
                    onLoadPrevious = {
                        onLoadPrevious()
                    },
                    refreshing = uiState.refreshing,
                    loadState = uiState.loadMoreState,
                    contentPadding = PaddingValues(
                        bottom = 20.dp,
                    )
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
