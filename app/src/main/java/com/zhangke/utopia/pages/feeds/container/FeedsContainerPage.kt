package com.zhangke.utopia.pages.feeds.container

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.utopia.pages.feeds.FeedsPage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedsContainerPage(
    uiState: FeedsContainerUiState,
    onTabSelected: (Int) -> Unit,
    onAddFeedsClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LoadableLayout(state = uiState.pageUiStateList) { feedsList ->
            if (feedsList.isNotEmpty()) {
                TabRow(
                    selectedTabIndex = uiState.tabIndex,
                ) {
                    feedsList.forEachIndexed { index, item ->
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
                    pageCount = feedsList.size,
                    userScrollEnabled = true,
                ) { currentPage ->
                    if (currentPageIndex != currentPage) {
                        currentPageIndex = currentPage
                        LaunchedEffect(currentPageIndex) {
                            onTabSelected(currentPageIndex)
                        }
                    }
                    FeedsPage(uiState = feedsList[currentPage])
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
