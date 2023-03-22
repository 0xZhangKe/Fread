package com.zhangke.utopia.pages.feeds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FeedsContainerPage(
    uiState: FeedsContainerUiState,
    onTabSelected: (Int) -> Unit,
    onPageChanged: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (uiState.channelList.isNotEmpty()) {
            TabRow(
                selectedTabIndex = uiState.tabIndex,
            ) {
                uiState.channelList.forEachIndexed { index, item ->
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
                count = uiState.channelList.size,
                userScrollEnabled = true,
            ) {
                if (currentPageIndex != currentPage) {
                    currentPageIndex = currentPage
                    LaunchedEffect(currentPageIndex) {
                        onPageChanged(currentPageIndex)
                    }
                }

            }
        }
    }
}