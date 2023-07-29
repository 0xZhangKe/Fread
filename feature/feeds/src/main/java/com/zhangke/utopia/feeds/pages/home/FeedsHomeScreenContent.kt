package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPage
import com.zhangke.utopia.status.server.StatusProviderServer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun FeedsHomeScreenContent(
    uiState: FeedsHomeUiState,
    onTabSelected: (Int) -> Unit,
    onAddFeedsClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onServerItemClick: (StatusProviderServer) -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState()
    var topBarItems: List<StatusProviderServer> by remember {
        mutableStateOf(emptyList())
    }
    var showSelectSourcePopup by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            if (topBarItems.isNotEmpty()) {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    if (topBarItems.size > 1) {
                                        showSelectSourcePopup = true
                                    } else {
                                        onServerItemClick(topBarItems.first())
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = topBarItems.first().name,
                                fontSize = 18.sp,
                            )
                            if (topBarItems.size > 1) {
                                Icon(
                                    modifier = Modifier.padding(start = 4.dp),
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "",
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSelectSourcePopup,
                            onDismissRequest = { showSelectSourcePopup = false },
                        ) {
                            topBarItems.forEach { source ->
                                DropdownMenuItem(
                                    onClick = { onServerItemClick(source) },
                                ) {
                                    Text(text = source.name)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(),
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            LoadableLayout(
                modifier = Modifier.fillMaxSize(),
                state = uiState.pageUiStateList,
            ) { feedsList ->
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
                                    Box(
                                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                                    ) {
                                        Text(
                                            text = item.name,
                                        )
                                    }
                                }
                            }
                        }

                        val pagerState = rememberPagerState(
                            initialPage = uiState.tabIndex,
                            pageCount = feedsList::size,
                        )
                        val currentPage = pagerState.currentPage
                        LaunchedEffect(currentPage) {
                            onTabSelected(currentPage)
                        }
                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                            userScrollEnabled = true,
                        ) { pageIndex ->
                            val pagedUiState = feedsList[pageIndex]
                            topBarItems = pagedUiState.serverList
                            FeedsPage(
                                uiState = pagedUiState,
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
