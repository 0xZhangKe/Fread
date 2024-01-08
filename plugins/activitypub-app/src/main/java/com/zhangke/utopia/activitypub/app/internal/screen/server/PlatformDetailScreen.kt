package com.zhangke.utopia.activitypub.app.internal.screen.server

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.textString
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import kotlinx.coroutines.launch

@Destination(PlatformDetailRoute.ROUTE)
class PlatformDetailScreen(
    @Router private val route: String = "",
) : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel: ServerDetailViewModel = getViewModel()
        LaunchedEffect(route) {
            viewModel.serverBaseUrl = PlatformDetailRoute.parseBaseUrl(route)
            viewModel.onPageResume()
        }
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        ServiceDetailContent(
            uiState = uiState,
            onBackClick = navigator::pop,
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ServiceDetailContent(
        uiState: ServerDetailUiState,
        onBackClick: () -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val instance = uiState.instance
        InstanceDetailScaffold(
            instance = instance,
            loading = uiState.loading,
            onBackClick = onBackClick,
        ) { contentCanScrollBackward ->
            Column {
                val tabs = uiState.tabs
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = tabs::size,
                )
                TabRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    selectedTabIndex = pagerState.currentPage,
                ) {
                    tabs.forEachIndexed { index, item ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                        ) {
                            Box(
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            ) {
                                Text(
                                    text = textString(item.title),
                                )
                            }
                        }
                    }
                }
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    state = pagerState,
                ) { currentPage ->
                    // TODO check this, why need Screen params but receiver
                    if (uiState.baseUrl != null) {
                        tabs[currentPage].content(
                            this@PlatformDetailScreen,
                            uiState.baseUrl,
                            instance.rules,
                            contentCanScrollBackward,
                        )
                    }
                }
            }
        }
    }
}
