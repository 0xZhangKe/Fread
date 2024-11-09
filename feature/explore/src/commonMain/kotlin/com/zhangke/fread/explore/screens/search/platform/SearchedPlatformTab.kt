package com.zhangke.fread.explore.screens.search.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.DefaultLoading
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.explore.Res
import com.zhangke.fread.explore.explorer_search_tab_title_server
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.search.SearchContentResult
import com.zhangke.fread.status.ui.source.SearchContentResultUi
import org.jetbrains.compose.resources.stringResource

class SearchedPlatformTab(private val role: IdentityRole, private val query: String) :
    BasePagerTab() {
    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(Res.string.explorer_search_tab_title_server),
        )

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val viewModel = with(screen) {
            getViewModel<SearchPlatformViewModel, SearchPlatformViewModel.Factory> {
                it.create(role, query)
            }
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
        onContentClick: (SearchContentResult) -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.searching && uiState.searchedList.isEmpty()) {
                DefaultLoading()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.searchedList) {
                        SearchContentResultUi(content = it, onContentClick = onContentClick)
                    }
                }
            }
        }
    }
}
