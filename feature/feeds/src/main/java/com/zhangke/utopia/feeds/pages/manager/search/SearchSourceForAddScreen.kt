package com.zhangke.utopia.feeds.pages.manager.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.SearchToolbar
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.composable.StatusSourceNode
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.status.uri.FormalUri

internal class SearchSourceForAddScreen(
    private val onUrisAdded: (uris: List<FormalUri>) -> Unit,
) : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SearchSourceForAddViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        SearchSourceForAdd(
            loadableState = uiState,
            onBackClick = navigator::pop,
            onSearchClick = viewModel::onSearchClick,
            onAddClick = {
                onUrisAdded(listOf(it.uri))
                navigator.pop()
            },
        )
    }

    @Composable
    internal fun SearchSourceForAdd(
        loadableState: LoadableState<List<StatusSourceUiState>>,
        onBackClick: () -> Unit,
        onSearchClick: (query: String) -> Unit,
        onAddClick: (StatusSourceUiState) -> Unit,
    ) {
        BackHandler(true) {
            onBackClick()
        }
        SearchToolbar(
            onBackClick = onBackClick,
            placeholderText = stringResource(R.string.search_feeds_title_hint),
            onQueryChange = {},
            onSearch = {
                onSearchClick(it)
            },
            content = {
                LoadableLayout(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = loadableState
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 15.dp),
                    ) {
                        items(it) { item ->
                            StatusSourceNode(
                                modifier = Modifier.clickable {
                                    onAddClick(item)
                                },
                                item,
                            )
                        }
                    }
                }
            },
        )
    }
}
