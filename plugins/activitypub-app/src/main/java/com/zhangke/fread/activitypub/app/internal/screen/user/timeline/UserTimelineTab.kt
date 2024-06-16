package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.commonbiz.shared.composable.EmptyListContent
import com.zhangke.fread.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.fread.commonbiz.shared.composable.InitErrorContent
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusListPlaceholder

class UserTimelineTab(
    private val tabType: UserTimelineTabType,
    private val contentCanScrollBackward: MutableState<Boolean>,
    private val role: IdentityRole,
    private val userWebFinger: WebFinger,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() {
            val title = when (tabType) {
                UserTimelineTabType.POSTS -> R.string.activity_pub_user_detail_tab_post
                UserTimelineTabType.REPLIES -> R.string.activity_pub_user_detail_tab_replies
                UserTimelineTabType.MEDIA -> R.string.activity_pub_user_detail_tab_media
            }
            return PagerTabOptions(title = stringResource(title))
        }

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val viewModel = getViewModel<UserTimelineContainerViewModel>().getSubViewModel(
            tabType = tabType,
            role = role,
            webFinger = userWebFinger,
        )
        val uiState by viewModel.uiState.collectAsState()
        UserTimelineTabContent(
            uiState = uiState,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            nestedScrollConnection = nestedScrollConnection,
            contentCanScrollBackward = contentCanScrollBackward,
        )
        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun UserTimelineTabContent(
        uiState: UserTimelineUiState,
        composedStatusInteraction: ComposedStatusInteraction,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        nestedScrollConnection: NestedScrollConnection?,
        contentCanScrollBackward: MutableState<Boolean>?,
    ) {
        if (uiState.feeds.isEmpty()) {
            if (uiState.showPagingLoadingPlaceholder) {
                StatusListPlaceholder()
            } else if (uiState.pageErrorContent != null) {
                InitErrorContent(uiState.pageErrorContent)
            } else {
                EmptyListContent()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                val state = rememberLoadableInlineVideoLazyColumnState(
                    refreshing = uiState.refreshing,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                )
                val lazyListState = state.lazyListState
                if (contentCanScrollBackward != null) {
                    val canScrollBackward by remember {
                        derivedStateOf {
                            lazyListState.firstVisibleItemIndex != 0 || lazyListState.firstVisibleItemScrollOffset != 0
                        }
                    }
                    contentCanScrollBackward.value = canScrollBackward
                }
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
                            item.status.status.id
                        },
                    ) { index, item ->
                        UserTimelineItem(
                            item = item,
                            composedStatusInteraction = composedStatusInteraction,
                            indexInList = index,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun UserTimelineItem(
        item: UserTimelineStatus,
        composedStatusInteraction: ComposedStatusInteraction,
        indexInList: Int,
    ) {
        if (item.pinned) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(com.zhangke.fread.statusui.R.drawable.ic_keep),
                    contentDescription = "",
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(id = R.string.activity_pub_user_pinned_post),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        FeedsStatusNode(
            modifier = Modifier.fillMaxWidth(),
            status = item.status,
            composedStatusInteraction = composedStatusInteraction,
            indexInList = indexInList,
        )
    }
}
