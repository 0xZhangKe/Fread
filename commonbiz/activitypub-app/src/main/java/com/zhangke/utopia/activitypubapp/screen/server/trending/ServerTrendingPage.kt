package com.zhangke.utopia.activitypubapp.screen.server.trending

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.canScrollBackward
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.status.status.Status
import kotlinx.coroutines.flow.Flow

@Composable
internal fun Screen.ServerTrendingPage(
    host: String,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val viewModel = getViewModel<ServerTrendingViewModel>()
    viewModel.host = host
    ServerTrendingContent(
        statusFlow = viewModel.statusFlow,
        contentCanScrollBackward = contentCanScrollBackward,
    )
}

@Composable
private fun ServerTrendingContent(
    statusFlow: Flow<PagingData<Status>>,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val listState = rememberLazyListState()
    contentCanScrollBackward.value = canScrollBackward(listState)
    val statusList = statusFlow.collectAsLazyPagingItems()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 20.dp)
    ) {
        itemsIndexed(statusList) { index, status ->
            if (status != null) {
                FeedsStatusNode(
                    modifier = Modifier.padding(bottom = 15.dp),
                    status = status,
                    indexInList = index,
                )
            }
        }
    }
}
