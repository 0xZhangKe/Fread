package com.zhangke.fread.activitypub.app.internal.screen.user.tags

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreen
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreenKey
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.hashtag.HashtagUi
import com.zhangke.fread.status.ui.hashtag.HashtagUiPlaceholder
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class TagListScreenKey(
    val locator: PlatformLocator,
) : NavKey

@Composable
fun TagListScreen(viewModel: TagListViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = rememberSnackbarHostState()
    TagListContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = backStack::removeLastOrNull,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onTagClick = { hashtag ->
            backStack.add(
                HashtagTimelineScreenKey(
                    locator = uiState.locator,
                    hashtag = hashtag.name.removePrefix("#"),
                )
            )
        },
    )
    ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessageFlow)
}

@Composable
private fun TagListContent(
    uiState: TagListUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onTagClick: (Hashtag) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.activity_pub_followed_tags_screen_title),
                onBackClick = onBackClick,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        val loadableState = rememberLoadableLazyColumnState(
            refreshing = uiState.refreshing,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
        )
        LoadableLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = loadableState,
            refreshing = uiState.refreshing,
            loadState = uiState.loadState,
        ) {
            if (uiState.tags.isNotEmpty()) {
                items(uiState.tags) { tag ->
                    HashtagUi(
                        tag = tag,
                        onClick = onTagClick,
                    )
                }
            } else if (uiState.refreshing) {
                items(20) {
                    HashtagUiPlaceholder()
                }
            }
        }
    }
}
