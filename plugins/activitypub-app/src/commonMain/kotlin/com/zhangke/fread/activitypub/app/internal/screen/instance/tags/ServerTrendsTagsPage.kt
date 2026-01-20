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
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreenKey
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.hashtag.HashtagUi
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ServerTrendsTagsPage(
    baseUrl: FormalBaseUrl,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val viewModel = koinViewModel<ServerTrendsTagsViewModel>()
    viewModel.baseUrl = baseUrl
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onPageResume()
    }
    ServerTrendsTagsContent(
        uiState = uiState,
        contentCanScrollBackward = contentCanScrollBackward,
        onHashtagClick = { tag ->
            val locator = PlatformLocator(accountUri = null, baseUrl = baseUrl)
            backStack.add(
                HashtagTimelineScreenKey(
                    locator = locator,
                    hashtag = tag.name.removePrefix("#"),
                )
            )
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
