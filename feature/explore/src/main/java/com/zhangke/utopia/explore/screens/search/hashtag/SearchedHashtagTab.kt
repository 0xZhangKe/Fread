package com.zhangke.utopia.explore.screens.search.hashtag

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.explore.R
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.ui.hashtag.HashtagUi

class SearchedHashtagTab(private val query: String) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(R.string.explorer_search_tab_title_hashtag),
        )

    @Composable
    override fun Screen.TabContent() {
        val viewModel = getViewModel<SearchHashtagViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(query) {
            viewModel.onRefresh(query)
        }

        SearchedHashtagContent(
            uiState = uiState,
            onRefresh = {
                viewModel.onRefresh(query)
            },
            onLoadMore = {
                viewModel.onLoadMore(query)
            },
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun SearchedHashtagContent(
        uiState: CommonLoadableUiState<Hashtag>,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
    ) {
        val state = rememberLoadableInlineVideoLazyColumnState(
            refreshing = uiState.refreshing,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
        )
        LoadableInlineVideoLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
            refreshing = uiState.refreshing,
            loading = uiState.loadMoreState == LoadState.Loading,
        ) {
            itemsIndexed(uiState.dataList) { _, item ->
                HashtagUi(
                    modifier = Modifier.fillMaxWidth(),
                    tag = item,
                )
            }
        }
    }
}
