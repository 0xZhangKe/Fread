package com.zhangke.fread.feeds.pages.manager.search

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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.SearchToolbar
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.feeds.R
import com.zhangke.fread.feeds.composable.StatusSourceNode
import com.zhangke.fread.feeds.composable.StatusSourceUiState

internal class SearchSourceForAddScreen : BaseScreen() {

    companion object {

        internal const val SCREEN_KEY =
            "com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddScreen"
    }

    override val key: ScreenKey
        get() = SCREEN_KEY

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val resultNavigator = navigator.navigationResult
        val viewModel: SearchSourceForAddViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        SearchSourceForAdd(
            loadableState = uiState,
            onBackClick = navigator::pop,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchClick = viewModel::onSearchClick,
            onAddClick = {
                resultNavigator.popWithResult(it.uri)
            },
        )
    }

    @Composable
    internal fun SearchSourceForAdd(
        loadableState: LoadableState<List<StatusSourceUiState>>,
        onBackClick: () -> Unit,
        onQueryChanged: (String) -> Unit,
        onSearchClick: (query: String) -> Unit,
        onAddClick: (StatusSourceUiState) -> Unit,
    ) {
        BackHandler(true) {
            onBackClick()
        }
        SearchToolbar(
            onBackClick = onBackClick,
            placeholderText = stringResource(R.string.search_feeds_title_hint),
            onQueryChange = onQueryChanged,
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
