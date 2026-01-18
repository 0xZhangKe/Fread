package com.zhangke.fread.explore.screens.search.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.DefaultLoading
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.search.SearchedPlatform
import com.zhangke.fread.status.ui.source.SearchPlatformResultUi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal class SearchedPlatformTab(
    private val locator: PlatformLocator,
    private val query: String,
) : BaseTab() {

    override val options: TabOptions
        @Composable get() = TabOptions(
            title = stringResource(LocalizedString.explorerSearchTabTitleServer),
        )

    @Composable
    override fun Content() {
        super.Content()
        val viewModel = koinViewModel<SearchPlatformViewModel> {
            parametersOf(locator, query)
        }
        val uiState by viewModel.uiState.collectAsState()
        SearchedSourcesContent(
            uiState = uiState,
            onContentClick = viewModel::onContentClick,
        )
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
    }

    @Composable
    private fun SearchedSourcesContent(
        uiState: SearchedPlatformUiState,
        onContentClick: (SearchedPlatform) -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.searching && uiState.searchedList.isEmpty()) {
                DefaultLoading()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.searchedList) {
                        SearchPlatformResultUi(searchedResult = it, onContentClick = onContentClick)
                    }
                }
            }
        }
    }
}
