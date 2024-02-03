package com.zhangke.utopia.activitypub.app.internal.screen.instance.tags

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.ui.hashtag.HashtagUi

@Composable
internal fun Screen.ServerTrendsTagsPage(
    baseUrl: FormalBaseUrl,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val viewModel = getViewModel<ServerTrendsTagsViewModel>()
    viewModel.baseUrl = baseUrl
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onPageResume()
    }
    ServerTrendsTagsContent(
        uiState = uiState,
        contentCanScrollBackward = contentCanScrollBackward,
    )
}

@Composable
private fun ServerTrendsTagsContent(
    uiState: ServerTrendsTagsUiState,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val listState = rememberLazyListState()
    val canScrollBackward by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    contentCanScrollBackward.value = canScrollBackward
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        items(uiState.list) { item ->
            HashtagUi(item)
        }
    }
}
