package com.zhangke.utopia.pages.feeds

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.LoadableLayout

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedsContainerPage(
    uiState: FeedsContainerUiState,
    onTabSelected: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LoadableLayout(state = uiState.channelList) { channelList ->
            if (channelList.isNotEmpty()) {
                TabRow(
                    selectedTabIndex = uiState.tabIndex,
                ) {
                    channelList.forEachIndexed { index, item ->
                        Text(
                            modifier = Modifier.clickable { onTabSelected(index) },
                            text = item.name,
                        )
                    }
                }
                val pagerState = rememberPagerState(uiState.tabIndex)
                var currentPageIndex by remember {
                    mutableStateOf(uiState.tabIndex)
                }
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                    pageCount = channelList.size,
                    userScrollEnabled = true,
                ) { currentPage ->
                    if (currentPageIndex != currentPage) {
                        currentPageIndex = currentPage
                        LaunchedEffect(currentPageIndex) {
                            onTabSelected(currentPageIndex)
                        }
                    }
                    FeedsPage(uiState = channelList[currentPage])
                }
            }
        }
    }
}
