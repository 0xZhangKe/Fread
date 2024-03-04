package com.zhangke.utopia.feeds.pages.manager.add.pre

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.CardInfoSection
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.status.search.SearchContentResult
import com.zhangke.utopia.status.ui.BlogPlatformUi
import kotlinx.coroutines.flow.Flow

class PreAddFeedsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<PreAddFeedsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        PreAddFeedsContent(
            uiState = uiState,
            snackBarMessageFlow = viewModel.snackBarMessageFlow,
            onBackClick = navigator::pop,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchClick = viewModel::onSearchClick,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PreAddFeedsContent(
        uiState: PreAddFeedsUiState,
        snackBarMessageFlow: Flow<TextString>,
        onBackClick: () -> Unit,
        onQueryChanged: (String) -> Unit,
        onSearchClick: (String) -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        ConsumeSnackbarFlow(snackbarHostState, snackBarMessageFlow)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(id = R.string.add_feeds_page_title),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { innerPaddings ->
            Box(
                modifier = Modifier
                    .padding(innerPaddings)
                    .fillMaxSize()
            ) {
                SearchBar(
                    modifier = Modifier.padding(start = 22.dp, top = 36.dp, end = 22.dp),
                    query = uiState.query,
                    onQueryChange = onQueryChanged,
                    onSearch = onSearchClick,
                    active = true,
                    onActiveChange = {},
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.allSearchedResult) { content ->
                            SearchContentResultUi(content)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SearchContentResultUi(
        content: SearchContentResult,
    ) {
        when (content) {
            is SearchContentResult.Source -> StatusSourceUi(content)
            is SearchContentResult.ActivityPubPlatform -> {
                BlogPlatformUi(
                    modifier = Modifier,
                    platform = content.platform,
                )
            }
        }
    }

    @Composable
    private fun StatusSourceUi(
        searchedSource: SearchContentResult.Source,
    ) {
        val source = searchedSource.source
        CardInfoSection(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            avatar = source.thumbnail,
            title = source.name,
            description = source.description,
        )
    }
}
