package com.zhangke.utopia.activitypub.app.internal.screen.server.trending

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.canScrollBackward
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.Flow

@Composable
internal fun Screen.ServerTrendingPage(
    baseUrl: FormalBaseUrl,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val viewModel = getViewModel<ServerTrendingViewModel>()
    viewModel.baseUrl = baseUrl
    LaunchedEffect(Unit) {
        viewModel.onPrepared()
    }
    val statusFlow by viewModel.statusFlow.collectAsState()
    LoadableLayout(
        state = statusFlow,
    ) {
        ServerTrendingContent(
            statusFlow = it,
            contentCanScrollBackward = contentCanScrollBackward,
            onInteractive = viewModel::onInteractive,
        )
    }
}

@Composable
private fun ServerTrendingContent(
    statusFlow: Flow<PagingData<StatusUiState>>,
    contentCanScrollBackward: MutableState<Boolean>,
    onInteractive: (Status, StatusUiInteraction) -> Unit,
) {
    val listState = rememberLazyListState()
    contentCanScrollBackward.value = canScrollBackward(listState)
    val statusList = statusFlow.collectAsLazyPagingItems()
    InlineVideoLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 20.dp)
    ) {
        itemsIndexed(statusList) { index, status ->
            if (status != null) {
                FeedsStatusNode(
                    modifier = Modifier.fillMaxWidth(),
                    status = status,
                    onInteractive = onInteractive,
                    indexInList = index,
                )
            }
        }
    }
}
