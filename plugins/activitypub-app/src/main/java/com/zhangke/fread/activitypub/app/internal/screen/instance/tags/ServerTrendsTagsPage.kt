package com.zhangke.fread.activitypub.app.internal.screen.instance.tags

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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.voyager.tryPush
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineRoute
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.hashtag.HashtagUi

@Composable
internal fun Screen.ServerTrendsTagsPage(
    baseUrl: FormalBaseUrl,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel = getViewModel<ServerTrendsTagsViewModel>()
    viewModel.baseUrl = baseUrl
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onPageResume()
    }
    ServerTrendsTagsContent(
        uiState = uiState,
        contentCanScrollBackward = contentCanScrollBackward,
        onHashtagClick = { tag ->
            val role = IdentityRole(accountUri = null, baseUrl = baseUrl)
            HashtagTimelineRoute.buildRoute(role, tag.name).let {
                navigator.tryPush(it)
            }
        },
    )
}

@Composable
private fun ServerTrendsTagsContent(
    uiState: ServerTrendsTagsUiState,
    contentCanScrollBackward: MutableState<Boolean>,
    onHashtagClick: (Hashtag) -> Unit,
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
            HashtagUi(
                tag = item,
                onClick = onHashtagClick,
            )
        }
    }
}
