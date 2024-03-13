package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.composable.textString
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.utils.LoadState
import com.zhangke.framework.utils.pxToDp
import com.zhangke.utopia.activitypub.app.internal.composable.ActivityPubStatusUi
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.model.Status

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ActivityPubListStatusContent(
    uiState: CommonLoadableUiState<StatusUiState>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onInteractive: (Status, StatusUiInteraction) -> Unit,
    canScrollBackward: MutableState<Boolean>? = null,
    onVoted: (Status, List<BlogPoll.Option>) -> Unit,
    nestedScrollConnection: NestedScrollConnection? = null,
) {
    val state = rememberLoadableInlineVideoLazyColumnState(
        refreshing = uiState.refreshing,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
    )
    canScrollBackward?.value = state.lazyListState.canScrollBackward
    val errorMessage = uiState.errorMessage?.let { textString(it) }
    var containerHeight: Dp? by remember {
        mutableStateOf(null)
    }
    val density = LocalDensity.current
    Box(modifier = Modifier.fillMaxSize()) {
        LoadableInlineVideoLazyColumn(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    containerHeight = it.size.height.pxToDp(density)
                }
                .applyNestedScrollConnection(nestedScrollConnection),
            refreshing = uiState.refreshing,
            loadState = uiState.loadMoreState,
            contentPadding = PaddingValues(
                bottom = 20.dp,
            )
        ) {
            itemsIndexed(
                items = uiState.dataList,
                key = { _, item ->
                    item.status.id
                },
            ) { index, status ->
                ActivityPubStatusUi(
                    modifier = Modifier.fillMaxWidth(),
                    status = status,
                    onInteractive = onInteractive,
                    indexInList = index,
                    onVoted = onVoted,
                )
            }
            if (!errorMessage.isNullOrEmpty() && uiState.dataList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .run {
                                if (containerHeight != null) {
                                    fillMaxWidth().height(containerHeight!!)
                                } else {
                                    fillMaxSize()
                                }
                            },
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = errorMessage,
                        )
                    }
                }
            }
        }
    }
}
