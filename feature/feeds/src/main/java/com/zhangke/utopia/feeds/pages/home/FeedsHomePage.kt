package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPage

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun FeedsHomePage(
    uiState: FeedsHomeUiState,
    onTabSelected: (Int) -> Unit,
    onAddFeedsClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    modifier = Modifier.padding(end = 15.dp),
                    onClick = onAddFeedsClick,
                ) {
                    Text(text = "Add Feeds")
                }
            }
            LoadableLayout(state = uiState.pageUiStateList) { feedsList ->
                if (feedsList.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(
                            modifier = Modifier
                                .height(58.dp)
                                .fillMaxWidth(),
                            selectedTabIndex = uiState.tabIndex,
                        ) {
                            feedsList.forEachIndexed { index, item ->
                                Tab(
                                    selected = uiState.tabIndex == index,
                                    onClick = { onTabSelected(index) },
                                ) {
                                    Text(
                                        text = item.name,
                                    )
                                }
                            }
                        }
                        val pagerState = rememberPagerState(uiState.tabIndex)
                        var currentPageIndex: Int? by remember {
                            mutableStateOf(null)
                        }
                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                            pageCount = feedsList.size,
                            userScrollEnabled = true,
                        ) { currentPage ->
                            if (currentPageIndex != currentPage) {
                                currentPageIndex = currentPage
                                val localPageIndex = currentPageIndex
                                if (localPageIndex != null) {
                                    LaunchedEffect(localPageIndex) {
                                        onTabSelected(localPageIndex)
                                    }
                                }
                            }
                            FeedsPage(
                                uiState = feedsList[currentPage],
                                onRefresh = onRefresh,
                                onLoadMore = onLoadMore,
                                onShowSnackMessage = {
                                    snackbarHostState.showSnackbar(it)
                                }
                            )
                        }
                    }
                } else {
                    Button(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = onAddFeedsClick,
                    ) {
                        Text(text = "Add Feeds")
                    }
                }
            }
        }
    }
}
