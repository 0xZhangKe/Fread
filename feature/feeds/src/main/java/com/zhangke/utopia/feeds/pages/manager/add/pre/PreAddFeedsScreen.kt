package com.zhangke.utopia.feeds.pages.manager.add.pre

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.CardInfoSection
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.commonbiz.shared.screen.login.LoginBottomSheetScreen
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.status.search.SearchContentResult
import com.zhangke.utopia.status.ui.BlogPlatformUi

/**
 * 添加 Feeds 预先搜索页，用于输入内容，判断类型添加内容。
 */
class PreAddFeedsScreen : Screen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel = getViewModel<PreAddFeedsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = rememberSnackbarHostState()
        PreAddFeedsContent(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackClick = navigator::pop,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchClick = viewModel::onSearchClick,
            onContentClick = viewModel::onContentClick,
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.pop()
            navigator.push(it)
        }
        ConsumeFlow(viewModel.loginRecommendPlatform) {
            bottomSheetNavigator.show(LoginBottomSheetScreen(it))
        }
        ConsumeFlow(viewModel.addContentSuccessFlow) {
            snackbarHostState.showSnackbar(context.getString(R.string.add_content_success_snackbar))
            navigator.pop()
        }
        ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessageFlow)
    }

    @Composable
    private fun PreAddFeedsContent(
        uiState: PreAddFeedsUiState,
        snackbarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onQueryChanged: (String) -> Unit,
        onSearchClick: () -> Unit,
        onContentClick: (SearchContentResult) -> Unit,
    ) {
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
            Column(
                modifier = Modifier
                    .padding(innerPaddings)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, top = 36.dp, end = 22.dp),
                    value = uiState.query,
                    onValueChange = onQueryChanged,
                    maxLines = 1,
                    trailingIcon = {
                        SimpleIconButton(
                            onClick = onSearchClick,
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                ) {
                    items(uiState.allSearchedResult) { content ->
                        SearchContentResultUi(content, onContentClick)
                    }
                }
            }
        }
    }

    @Composable
    private fun SearchContentResultUi(
        content: SearchContentResult,
        onContentClick: (SearchContentResult) -> Unit,
    ) {
        when (content) {
            is SearchContentResult.Source -> StatusSourceUi(content, onContentClick)
            is SearchContentResult.ActivityPubPlatform -> {
                BlogPlatformUi(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onContentClick(content) },
                    platform = content.platform,
                )
            }
        }
    }

    @Composable
    private fun StatusSourceUi(
        searchedSource: SearchContentResult.Source,
        onContentClick: (SearchContentResult) -> Unit,
    ) {
        val source = searchedSource.source
        CardInfoSection(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .clickable { onContentClick(searchedSource) },
            avatar = source.thumbnail,
            title = source.name,
            description = source.description,
        )
    }
}
